package com.resolveos.domain.identity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Membership(
        UUID id,
        UUID organizationId,
        String subjectId,
        OrganizationRole role,
        MembershipStatus status,
        Instant createdAt) {

    public Membership {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(organizationId, "organizationId must not be null");
        Objects.requireNonNull(subjectId, "subjectId must not be null");
        Objects.requireNonNull(role, "role must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
    }
}
