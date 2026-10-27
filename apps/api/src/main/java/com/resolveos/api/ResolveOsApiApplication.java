package com.resolveos.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.resolveos")
@EntityScan("com.resolveos.persistence")
@EnableJpaRepositories("com.resolveos.persistence")
public class ResolveOsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResolveOsApiApplication.class, args);
    }
}
