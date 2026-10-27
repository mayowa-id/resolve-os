package com.resolveos.api.identity;

import com.resolveos.domain.identity.Organization;
import com.resolveos.domain.identity.OrganizationStatus;
import java.time.Instant;
import java.util.UUID;

public record OrganizationResponse(
        UUID id, String slug, String name, OrganizationStatus status, Instant createdAt) {

    static OrganizationResponse from(Organization organization) {
        return new OrganizationResponse(
                organization.id(),
                organization.slug(),
                organization.name(),
                organization.status(),
                organization.createdAt());
    }
}
