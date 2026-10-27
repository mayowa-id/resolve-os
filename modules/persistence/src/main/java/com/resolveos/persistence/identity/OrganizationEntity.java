package com.resolveos.persistence.identity;

import com.resolveos.domain.identity.OrganizationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "organizations")
class OrganizationEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 63)
    private String slug;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OrganizationStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected OrganizationEntity() {}

    UUID id() {
        return id;
    }

    String slug() {
        return slug;
    }

    String name() {
        return name;
    }

    OrganizationStatus status() {
        return status;
    }

    Instant createdAt() {
        return createdAt;
    }
}
