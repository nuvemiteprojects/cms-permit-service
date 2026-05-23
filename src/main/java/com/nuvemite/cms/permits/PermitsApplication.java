package com.nuvemite.cms.permits;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PermitsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PermitsApplication.class, args);
    }
}
