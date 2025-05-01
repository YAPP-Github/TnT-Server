package com.tnt.trainer.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerJpaRepository extends JpaRepository<TrainerJpaEntity, Long> {

	Optional<TrainerJpaEntity> findByMemberIdAndDeletedAtIsNull(Long memberId);

	boolean existsByMemberIdAndDeletedAtIsNull(Long memberId);

	boolean existsByInvitationCodeAndDeletedAtIsNull(String invitationCode);
}
