package com.resolveos.api.config;

import com.resolveos.application.identity.TenantAccessService;
import com.resolveos.application.identity.port.OrganizationAccessPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class IdentityConfiguration {

    @Bean
    TenantAccessService tenantAccessService(OrganizationAccessPort organizationAccessPort) {
        return new TenantAccessService(organizationAccessPort);
    }
}
