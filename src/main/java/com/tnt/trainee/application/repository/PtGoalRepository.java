package com.tnt.trainee.application.repository;

import java.util.List;

import com.tnt.trainee.domain.PtGoal;

public interface PtGoalRepository {

	List<PtGoal> saveAll(List<PtGoal> ptGoals);

	List<PtGoal> findAllByTraineeId(Long traineeId);

	void deleteAll(List<PtGoal> goalsToDelete);
}
