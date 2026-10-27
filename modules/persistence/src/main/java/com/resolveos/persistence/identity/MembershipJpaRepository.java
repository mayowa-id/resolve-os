package com.resolveos.persistence.identity;

import com.resolveos.domain.identity.MembershipStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface MembershipJpaRepository extends JpaRepository<MembershipEntity, UUID> {

    Optional<MembershipEntity> findByOrganizationIdAndSubjectIdAndStatus(
            UUID organizationId, String subjectId, MembershipStatus status);

    List<MembershipEntity> findAllByOrganizationIdAndStatusOrderBySubjectId(
            UUID organizationId, MembershipStatus status);
}
