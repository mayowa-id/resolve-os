package com.resolveos.application.identity;

public final class InsufficientOrganizationRoleException extends RuntimeException {

    public InsufficientOrganizationRoleException() {
        super("The active membership does not permit this operation");
    }
}
