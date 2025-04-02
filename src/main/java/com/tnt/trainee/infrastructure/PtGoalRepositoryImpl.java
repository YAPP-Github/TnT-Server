package com.tnt.trainee.infrastructure;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.tnt.trainee.application.repository.PtGoalRepository;
import com.tnt.trainee.domain.PtGoal;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PtGoalRepositoryImpl implements PtGoalRepository {

	private final PtGoalJpaRepository ptGoalJpaRepository;

	@Override
	public List<PtGoal> saveAll(List<PtGoal> ptGoals) {
		return ptGoalJpaRepository.saveAll(ptGoals);
	}

	@Override
	public List<PtGoal> findAllByTraineeId(Long traineeId) {
		return ptGoalJpaRepository.findAllByTraineeIdAndDeletedAtIsNull(traineeId);
	}
}
