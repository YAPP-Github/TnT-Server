package com.tnt.trainer.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tnt.trainer.domain.Trainer;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

	Optional<Trainer> findByMemberIdAndDeletedAtIsNull(Long memberId);

	boolean existsByMemberIdAndDeletedAtIsNull(Long memberId);

	boolean existsByInvitationCodeAndDeletedAtIsNull(String invitationCode);
}
