package com.tnt.trainee.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TraineeJpaRepository extends JpaRepository<TraineeJpaEntity, Long> {

	Optional<TraineeJpaEntity> findByMemberIdAndDeletedAtIsNull(Long memberId);
}
