package com.resolveos.application.identity.port;

import com.resolveos.domain.identity.Membership;
import com.resolveos.domain.identity.Organization;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationAccessPort {

    List<Organization> findOrganizationsForActiveSubject(String subjectId);

    Optional<Organization> findActiveOrganizationForSubject(UUID organizationId, String subjectId);

    Optional<Membership> findActiveMembership(UUID organizationId, String subjectId);

    List<Membership> findActiveMemberships(UUID organizationId);
}
