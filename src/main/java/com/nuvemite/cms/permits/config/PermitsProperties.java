package com.nuvemite.cms.permits.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cms.permits")
public record PermitsProperties(Outbox outbox, int permitValidityYears, Services services) {

    public record Outbox(long pollIntervalMs, int batchSize) {}

    public record Services(String companiesUrl, String chemicalManagementUrl) {}
}
