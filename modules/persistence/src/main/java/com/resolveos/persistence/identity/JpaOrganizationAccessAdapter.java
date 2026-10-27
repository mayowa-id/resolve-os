package com.resolveos.persistence.identity;

import com.resolveos.application.identity.port.OrganizationAccessPort;
import com.resolveos.domain.identity.Membership;
import com.resolveos.domain.identity.MembershipStatus;
import com.resolveos.domain.identity.Organization;
import com.resolveos.domain.identity.OrganizationStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class JpaOrganizationAccessAdapter implements OrganizationAccessPort {

    private final OrganizationJpaRepository organizations;
    private final MembershipJpaRepository memberships;

    public JpaOrganizationAccessAdapter(
            OrganizationJpaRepository organizations, MembershipJpaRepository memberships) {
        this.organizations = organizations;
        this.memberships = memberships;
    }

    @Override
    public List<Organization> findOrganizationsForActiveSubject(String subjectId) {
        return organizations
                .findForSubject(subjectId, MembershipStatus.ACTIVE, OrganizationStatus.ACTIVE)
                .stream()
                .map(JpaOrganizationAccessAdapter::toDomain)
                .toList();
    }

    @Override
    public Optional<Organization> findActiveOrganizationForSubject(UUID organizationId, String subjectId) {
        return organizations
                .findAccessibleOrganization(
                        organizationId, subjectId, MembershipStatus.ACTIVE, OrganizationStatus.ACTIVE)
                .map(JpaOrganizationAccessAdapter::toDomain);
    }

    @Override
    public Optional<Membership> findActiveMembership(UUID organizationId, String subjectId) {
        return memberships
                .findByOrganizationIdAndSubjectIdAndStatus(
                        organizationId, subjectId, MembershipStatus.ACTIVE)
                .map(JpaOrganizationAccessAdapter::toDomain);
    }

    @Override
    public List<Membership> findActiveMemberships(UUID organizationId) {
        return memberships
                .findAllByOrganizationIdAndStatusOrderBySubjectId(
                        organizationId, MembershipStatus.ACTIVE)
                .stream()
                .map(JpaOrganizationAccessAdapter::toDomain)
                .toList();
    }

    private static Organization toDomain(OrganizationEntity entity) {
        return new Organization(
                entity.id(), entity.slug(), entity.name(), entity.status(), entity.createdAt());
    }

    private static Membership toDomain(MembershipEntity entity) {
        return new Membership(
                entity.id(),
                entity.organizationId(),
                entity.subjectId(),
                entity.role(),
                entity.status(),
                entity.createdAt());
    }
}
