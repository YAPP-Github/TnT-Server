package com.tnt.workout.infrastructure;

import static com.tnt.common.jpa.DynamicQuery.generateEq;
import static com.tnt.workout.infrastructure.QWorkoutJpaEntity.workoutJpaEntity;

import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tnt.workout.application.repository.WorkoutRepository;
import com.tnt.workout.domain.BodyPart;
import com.tnt.workout.domain.Machine;
import com.tnt.workout.domain.Workout;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkoutRepositoryImpl implements WorkoutRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Workout> search(@Nullable String keyword, @Nullable List<BodyPart> bodyParts,
		@Nullable List<Machine> machines, @Nullable Long lastId, int size) {

		return jpaQueryFactory
			.selectFrom(workoutJpaEntity)
			.where(
				generateEq(keyword, workoutJpaEntity.name::contains),
				bodyPartsIn(bodyParts),
				machinesIn(machines),
				generateEq(lastId, workoutJpaEntity.id::lt)
			)
			.orderBy(workoutJpaEntity.id.desc())
			.limit((long)size + 1)
			.fetch()
			.stream()
			.map(WorkoutJpaEntity::toModel)
			.toList();
	}

	private BooleanExpression bodyPartsIn(@Nullable List<BodyPart> bodyParts) {
		if (bodyParts == null || bodyParts.isEmpty()) {
			return null;
		}
		// ElementCollection의 경우 any()를 사용하여 리스트 내의 요소가 포함되는지 확인
		BooleanExpression expression = null;

		for (BodyPart bodyPart : bodyParts) {
			BooleanExpression condition = workoutJpaEntity.bodyParts.any().eq(bodyPart);
			expression = expression == null ? condition : expression.or(condition);
		}

		return expression;
	}

	private BooleanExpression machinesIn(@Nullable List<Machine> machines) {
		if (machines == null || machines.isEmpty()) {
			return null;
		}
		// ElementCollection의 경우 any()를 사용하여 리스트 내의 요소가 포함되는지 확인
		BooleanExpression expression = null;

		for (Machine machine : machines) {
			BooleanExpression condition = workoutJpaEntity.machines.any().eq(machine);
			expression = expression == null ? condition : expression.or(condition);
		}

		return expression;
	}
}
