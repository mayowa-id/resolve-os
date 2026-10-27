package com.resolveos.application.identity;

import java.util.UUID;

public final class MembershipRequiredException extends RuntimeException {

    private final UUID organizationId;

    public MembershipRequiredException(UUID organizationId) {
        super("Active organization membership was not found");
        this.organizationId = organizationId;
    }

    public UUID organizationId() {
        return organizationId;
    }
}
