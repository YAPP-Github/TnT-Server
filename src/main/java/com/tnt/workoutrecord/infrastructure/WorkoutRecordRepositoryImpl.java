package com.tnt.workoutrecord.infrastructure;

import static com.tnt.common.error.model.ErrorMessage.WORKOUT_RECORD_NOT_FOUND;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tnt.common.error.exception.NotFoundException;
import com.tnt.workoutrecord.application.repository.WorkoutRecordRepository;
import com.tnt.workoutrecord.domain.WorkoutRecord;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkoutRecordRepositoryImpl implements WorkoutRecordRepository {

	private final WorkoutRecordJpaRepository workoutRecordJpaRepository;
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public WorkoutRecord save(WorkoutRecord workoutRecord) {
		return workoutRecordJpaRepository.save(WorkoutRecordJpaEntity.from(workoutRecord)).toModel();
	}

	@Override
	public WorkoutRecord findById(Long workoutRecordId) {
		return workoutRecordJpaRepository.findById(workoutRecordId)
			.map(WorkoutRecordJpaEntity::toModel)
			.orElseThrow(() -> new NotFoundException(WORKOUT_RECORD_NOT_FOUND));
	}

	@Override
	public void deleteById(Long workoutRecordId) {
		workoutRecordJpaRepository.deleteById(workoutRecordId);
	}
}
