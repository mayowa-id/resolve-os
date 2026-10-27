package com.resolveos.api.identity;

import com.resolveos.application.identity.TenantAccessService;
import com.resolveos.domain.identity.OrganizationRole;
import java.util.List;
import java.util.UUID;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class OrganizationController {

    private final TenantAccessService tenantAccess;

    public OrganizationController(TenantAccessService tenantAccess) {
        this.tenantAccess = tenantAccess;
    }

    @GetMapping("/me/organizations")
    List<OrganizationResponse> listMyOrganizations(JwtAuthenticationToken authentication) {
        return tenantAccess.listOrganizations(authentication.getToken().getSubject()).stream()
                .map(OrganizationResponse::from)
                .toList();
    }

    @GetMapping("/organizations/{organizationId}")
    OrganizationResponse getOrganization(
            @PathVariable UUID organizationId, JwtAuthenticationToken authentication) {
        return OrganizationResponse.from(tenantAccess.requireOrganization(
                organizationId, authentication.getToken().getSubject()));
    }

    @GetMapping("/organizations/{organizationId}/memberships")
    List<MembershipResponse> listMemberships(
            @PathVariable UUID organizationId, JwtAuthenticationToken authentication) {
        return tenantAccess
                .listMemberships(
                        organizationId,
                        authentication.getToken().getSubject(),
                        OrganizationRole.ORG_ADMIN,
                        OrganizationRole.OPERATIONS_MANAGER)
                .stream()
                .map(MembershipResponse::from)
                .toList();
    }
}
