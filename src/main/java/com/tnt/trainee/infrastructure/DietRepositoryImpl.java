package com.tnt.trainee.infrastructure;

import static com.tnt.common.error.model.ErrorMessage.DIET_NOT_FOUND;
import static com.tnt.trainee.infrastructure.QDietJpaEntity.dietJpaEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tnt.common.error.exception.NotFoundException;
import com.tnt.trainee.application.repository.DietRepository;
import com.tnt.trainee.domain.Diet;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DietRepositoryImpl implements DietRepository {

	private final DietJpaRepository dietJpaRepository;
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Diet save(Diet diet) {
		return dietJpaRepository.save(DietJpaEntity.from(diet)).toModel();
	}

	@Override
	public void saveAll(List<Diet> diets) {
		List<DietJpaEntity> dietJpaEntities = diets.stream()
			.map(DietJpaEntity::from)
			.toList();

		dietJpaRepository.saveAll(dietJpaEntities);
	}

	@Override
	public Diet findByIdAndTraineeId(Long id, Long traineeId) {
		return dietJpaRepository.findByIdAndTraineeIdAndDeletedAtIsNull(id, traineeId)
			.orElseThrow(() -> new NotFoundException(DIET_NOT_FOUND)).toModel();
	}

	@Override
	public List<Diet> findAllByTraineeId(Long traineeId) {
		return dietJpaRepository.findAllByTraineeIdAndDeletedAtIsNull(traineeId).stream()
			.map(DietJpaEntity::toModel)
			.toList();
	}

	@Override
	public List<Diet> findAllByTraineeIdForDaily(Long traineeId, LocalDate date) {
		return jpaQueryFactory
			.selectFrom(dietJpaEntity)
			.where(
				dietJpaEntity.traineeId.eq(traineeId),
				dietJpaEntity.date.between(date.atStartOfDay(), date.atTime(LocalTime.MAX)),
				dietJpaEntity.deletedAt.isNull()
			)
			.orderBy(dietJpaEntity.date.asc())
			.fetch()
			.stream()
			.map(DietJpaEntity::toModel)
			.toList();
	}

	@Override
	public List<Diet> findAllByTraineeIdForTraineeCalendar(Long traineeId, LocalDate startDate,
		LocalDate endDate) {
		return jpaQueryFactory
			.selectFrom(dietJpaEntity)
			.where(
				dietJpaEntity.traineeId.eq(traineeId),
				dietJpaEntity.date.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)),
				dietJpaEntity.deletedAt.isNull()
			)
			.orderBy(dietJpaEntity.date.asc())
			.fetch()
			.stream()
			.map(DietJpaEntity::toModel)
			.toList();
	}

	@Override
	public boolean existsByTraineeIdAndDate(Long traineeId, LocalDateTime date) {
		return dietJpaRepository.existsByTraineeIdAndDateAndDeletedAtIsNull(traineeId, date);
	}
}
