package com.resolveos.domain.identity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Organization(
        UUID id,
        String slug,
        String name,
        OrganizationStatus status,
        Instant createdAt) {

    public Organization {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(slug, "slug must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
    }
}
