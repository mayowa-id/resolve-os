package com.resolveos.persistence.identity;

import com.resolveos.domain.identity.MembershipStatus;
import com.resolveos.domain.identity.OrganizationStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface OrganizationJpaRepository extends JpaRepository<OrganizationEntity, UUID> {

    @Query("""
            select organization
            from OrganizationEntity organization, MembershipEntity membership
            where membership.organizationId = organization.id
              and membership.subjectId = :subjectId
              and membership.status = :membershipStatus
              and organization.status = :organizationStatus
            order by organization.name, organization.id
            """)
    List<OrganizationEntity> findForSubject(
            @Param("subjectId") String subjectId,
            @Param("membershipStatus") MembershipStatus membershipStatus,
            @Param("organizationStatus") OrganizationStatus organizationStatus);

    @Query("""
            select organization
            from OrganizationEntity organization, MembershipEntity membership
            where organization.id = :organizationId
              and membership.organizationId = organization.id
              and membership.subjectId = :subjectId
              and membership.status = :membershipStatus
              and organization.status = :organizationStatus
            """)
    Optional<OrganizationEntity> findAccessibleOrganization(
            @Param("organizationId") UUID organizationId,
            @Param("subjectId") String subjectId,
            @Param("membershipStatus") MembershipStatus membershipStatus,
            @Param("organizationStatus") OrganizationStatus organizationStatus);
}
