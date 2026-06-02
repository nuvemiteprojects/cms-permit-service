# syntax=docker/dockerfile:1.7
FROM docker.io/library/eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY . .

RUN test -f pom.xml || (echo "Missing pom.xml in cms-permit-service. Scaffold the Maven service before building this image." && exit 1)
RUN test -f mvnw || (echo "Missing mvnw in cms-permit-service. Scaffold the Maven service before building this image." && exit 1)
RUN test -d src || (echo "Missing src/ in cms-permit-service. Scaffold the Maven service before building this image." && exit 1)

RUN chmod +x mvnw
RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline
RUN --mount=type=cache,target=/root/.m2 ./mvnw package -DskipTests

FROM docker.io/library/eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
