package com.resolveos.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.resolveos")
public class ResolveOsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResolveOsApiApplication.class, args);
    }
}
