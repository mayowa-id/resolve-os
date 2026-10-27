package com.resolveos.api.identity;

import com.resolveos.domain.identity.Membership;
import com.resolveos.domain.identity.MembershipStatus;
import com.resolveos.domain.identity.OrganizationRole;
import java.time.Instant;
import java.util.UUID;

public record MembershipResponse(
        UUID id,
        UUID organizationId,
        String subjectId,
        OrganizationRole role,
        MembershipStatus status,
        Instant createdAt) {

    static MembershipResponse from(Membership membership) {
        return new MembershipResponse(
                membership.id(),
                membership.organizationId(),
                membership.subjectId(),
                membership.role(),
                membership.status(),
                membership.createdAt());
    }
}
