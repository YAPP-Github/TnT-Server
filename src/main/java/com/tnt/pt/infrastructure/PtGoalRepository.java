package com.tnt.pt.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tnt.trainee.domain.PtGoal;

public interface PtGoalRepository extends JpaRepository<PtGoal, Integer> {

	List<PtGoal> findAllByTraineeIdAndDeletedAtIsNull(Long traineeId);
}
