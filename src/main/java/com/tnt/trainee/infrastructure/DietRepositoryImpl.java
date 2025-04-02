package com.tnt.trainee.infrastructure;

import static com.tnt.common.error.model.ErrorMessage.DIET_NOT_FOUND;
import static com.tnt.trainee.domain.QDiet.diet;

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
		return dietJpaRepository.save(diet);
	}

	@Override
	public void saveAll(List<Diet> diets) {
		dietJpaRepository.saveAll(diets);
	}

	@Override
	public Diet findByIdAndTraineeId(Long id, Long traineeId) {
		return dietJpaRepository.findByIdAndTraineeIdAndDeletedAtIsNull(id, traineeId)
			.orElseThrow(() -> new NotFoundException(DIET_NOT_FOUND));
	}

	@Override
	public List<Diet> findAllByTraineeId(Long traineeId) {
		return dietJpaRepository.findAllByTraineeIdAndDeletedAtIsNull(traineeId);
	}

	@Override
	public List<Diet> findAllByTraineeIdForDaily(Long traineeId, LocalDate date) {
		return jpaQueryFactory
			.selectFrom(diet)
			.where(
				diet.traineeId.eq(traineeId),
				diet.date.between(date.atStartOfDay(), date.atTime(LocalTime.MAX)),
				diet.deletedAt.isNull()
			)
			.orderBy(diet.date.asc())
			.fetch();
	}

	@Override
	public List<Diet> findAllByTraineeIdForTraineeCalendar(Long traineeId, LocalDate startDate, LocalDate endDate) {
		return jpaQueryFactory
			.selectFrom(diet)
			.where(
				diet.traineeId.eq(traineeId),
				diet.date.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)),
				diet.deletedAt.isNull()
			)
			.orderBy(diet.date.asc())
			.fetch();
	}

	@Override
	public boolean existsByTraineeIdAndDate(Long traineeId, LocalDateTime date) {
		return dietJpaRepository.existsByTraineeIdAndDateAndDeletedAtIsNull(traineeId, date);
	}
}
