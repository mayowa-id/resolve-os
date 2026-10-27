package com.resolveos.application.identity;

import com.resolveos.application.identity.port.OrganizationAccessPort;
import com.resolveos.domain.identity.Membership;
import com.resolveos.domain.identity.Organization;
import com.resolveos.domain.identity.OrganizationRole;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class TenantAccessService {

    private final OrganizationAccessPort organizationAccess;

    public TenantAccessService(OrganizationAccessPort organizationAccess) {
        this.organizationAccess = Objects.requireNonNull(organizationAccess);
    }

    public List<Organization> listOrganizations(String subjectId) {
        return organizationAccess.findOrganizationsForActiveSubject(requireSubject(subjectId));
    }

    public Organization requireOrganization(UUID organizationId, String subjectId) {
        Objects.requireNonNull(organizationId, "organizationId must not be null");
        return organizationAccess
                .findActiveOrganizationForSubject(organizationId, requireSubject(subjectId))
                .orElseThrow(() -> new MembershipRequiredException(organizationId));
    }

    public Membership requireMembership(UUID organizationId, String subjectId) {
        Objects.requireNonNull(organizationId, "organizationId must not be null");
        return organizationAccess
                .findActiveMembership(organizationId, requireSubject(subjectId))
                .orElseThrow(() -> new MembershipRequiredException(organizationId));
    }

    public List<Membership> listMemberships(
            UUID organizationId, String subjectId, OrganizationRole firstRole, OrganizationRole... additionalRoles) {
        Membership actorMembership = requireMembership(organizationId, subjectId);
        Set<OrganizationRole> permittedRoles = EnumSet.of(firstRole, additionalRoles);
        if (!permittedRoles.contains(actorMembership.role())) {
            throw new InsufficientOrganizationRoleException();
        }
        return organizationAccess.findActiveMemberships(organizationId);
    }

    private static String requireSubject(String subjectId) {
        if (subjectId == null || subjectId.isBlank()) {
            throw new IllegalArgumentException("Authenticated subject must not be blank");
        }
        return subjectId;
    }
}
