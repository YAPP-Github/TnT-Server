package com.tnt.trainee.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tnt.trainee.domain.PtGoal;

public interface PtGoalJpaRepository extends JpaRepository<PtGoal, Long> {

	List<PtGoal> findAllByTraineeIdAndDeletedAtIsNull(Long traineeId);
}
