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
		List<PtGoalJpaEntity> ptGoalJpaEntities = ptGoals.stream()
			.map(PtGoalJpaEntity::from)
			.toList();

		return ptGoalJpaRepository.saveAll(ptGoalJpaEntities).stream()
			.map(PtGoalJpaEntity::toModel)
			.toList();
	}

	@Override
	public List<PtGoal> findAllByTraineeId(Long traineeId) {
		return ptGoalJpaRepository.findAllByTraineeId(traineeId).stream()
			.map(PtGoalJpaEntity::toModel)
			.toList();
	}

	@Override
	public void deleteAll(List<PtGoal> goalsToDelete) {
		List<PtGoalJpaEntity> ptGoalJpaEntities = goalsToDelete.stream()
			.map(PtGoalJpaEntity::from)
			.toList();

		ptGoalJpaRepository.deleteAll(ptGoalJpaEntities);
	}
}
