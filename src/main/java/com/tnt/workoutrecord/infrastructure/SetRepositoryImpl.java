package com.tnt.workoutrecord.infrastructure;

import static com.tnt.workoutrecord.infrastructure.QSetJpaEntity.setJpaEntity;

import java.util.List;

import org.springframework.stereotype.Service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tnt.workoutrecord.application.repository.SetRepository;
import com.tnt.workoutrecord.domain.Set;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SetRepositoryImpl implements SetRepository {

	private final SetJpaRepository setJpaRepository;
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Set> saveAll(List<Set> sets) {
		if (sets.isEmpty()) {
			return List.of();
		}

		List<SetJpaEntity> setJpaEntities = sets.stream().map(SetJpaEntity::from).toList();

		List<SetJpaEntity> saved = setJpaRepository.saveAll(setJpaEntities);

		return saved.stream().map(SetJpaEntity::toModel).toList();
	}

	@Override
	public List<Set> findAllByRoutineIds(List<Long> routineIds) {
		if (routineIds.isEmpty()) {
			return List.of();
		}

		return jpaQueryFactory
			.selectFrom(setJpaEntity)
			.where(setJpaEntity.routineId.in(routineIds))
			.fetch()
			.stream()
			.map(SetJpaEntity::toModel)
			.toList();
	}

	@Override
	public void deleteAllByRoutineIds(List<Long> routineIds) {
		if (routineIds.isEmpty()) {
			return;
		}

		jpaQueryFactory
			.delete(setJpaEntity)
			.where(setJpaEntity.routineId.in(routineIds))
			.execute();
	}
}
