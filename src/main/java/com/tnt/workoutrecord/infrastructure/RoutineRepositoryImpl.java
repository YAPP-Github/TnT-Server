package com.tnt.workoutrecord.infrastructure;

import static com.tnt.workoutrecord.infrastructure.QRoutineJpaEntity.routineJpaEntity;

import java.util.List;

import org.springframework.stereotype.Service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tnt.workoutrecord.application.repository.RoutineRepository;
import com.tnt.workoutrecord.domain.Routine;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoutineRepositoryImpl implements RoutineRepository {

	private final RoutineJpaRepository routineJpaRepository;
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Routine> saveAll(List<Routine> routines) {
		if (routines.isEmpty()) {
			return List.of();
		}

		List<RoutineJpaEntity> routineJpaEntities = routines.stream().map(RoutineJpaEntity::from).toList();

		List<RoutineJpaEntity> saved = routineJpaRepository.saveAll(routineJpaEntities);

		return saved.stream().map(RoutineJpaEntity::toModel).toList();
	}

	@Override
	public List<Routine> findAllByWorkoutRecordId(Long workoutRecordId) {
		return jpaQueryFactory
			.selectFrom(routineJpaEntity)
			.where(routineJpaEntity.workoutRecordId.eq(workoutRecordId))
			.fetch()
			.stream()
			.map(RoutineJpaEntity::toModel)
			.toList();
	}

	@Override
	public void deleteAllByWorkoutRecordId(Long workoutRecordId) {
		jpaQueryFactory
			.delete(routineJpaEntity)
			.where(routineJpaEntity.workoutRecordId.eq(workoutRecordId))
			.execute();
	}
}
