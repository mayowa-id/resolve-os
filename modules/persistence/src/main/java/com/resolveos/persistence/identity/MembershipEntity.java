package com.resolveos.persistence.identity;

import com.resolveos.domain.identity.MembershipStatus;
import com.resolveos.domain.identity.OrganizationRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "memberships")
class MembershipEntity {

    @Id
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "subject_id", nullable = false, length = 255)
    private String subjectId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 64)
    private OrganizationRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private MembershipStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected MembershipEntity() {}

    UUID id() {
        return id;
    }

    UUID organizationId() {
        return organizationId;
    }

    String subjectId() {
        return subjectId;
    }

    OrganizationRole role() {
        return role;
    }

    MembershipStatus status() {
        return status;
    }

    Instant createdAt() {
        return createdAt;
    }
}
