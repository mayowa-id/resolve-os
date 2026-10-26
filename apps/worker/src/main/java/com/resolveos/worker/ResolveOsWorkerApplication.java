package com.resolveos.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.resolveos")
public class ResolveOsWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResolveOsWorkerApplication.class, args);
    }
}
