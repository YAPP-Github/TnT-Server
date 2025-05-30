package com.tnt.trainee.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PtGoalJpaRepository extends JpaRepository<PtGoalJpaEntity, Long> {

	List<PtGoalJpaEntity> findAllByTraineeId(Long traineeId);
}
