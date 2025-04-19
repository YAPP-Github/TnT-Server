package com.tnt.pt.infrastructure;

import static com.tnt.member.infrastructure.QMemberJpaEntity.memberJpaEntity;
import static com.tnt.pt.infrastructure.QPtLessonJpaEntity.ptLessonJpaEntity;
import static com.tnt.pt.infrastructure.QPtTrainerTraineeJpaEntity.ptTrainerTraineeJpaEntity;
import static com.tnt.trainee.infrastructure.QTraineeJpaEntity.traineeJpaEntity;
import static com.tnt.trainer.infrastructure.QTrainerJpaEntity.trainerJpaEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tnt.common.error.exception.NotFoundException;
import com.tnt.common.error.model.ErrorMessage;
import com.tnt.pt.application.repository.PtLessonRepository;
import com.tnt.pt.domain.PtLesson;
import com.tnt.pt.domain.PtTrainerTrainee;
import com.tnt.pt.dto.PtTrainerTraineeProjection;
import com.tnt.pt.dto.QPtTrainerTraineeProjection_PtInfoDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PtLessonRepositoryImpl implements PtLessonRepository {

	private final PtLessonJpaRepository ptLessonJpaRepository;
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public PtLesson save(PtLesson ptLesson) {
		return ptLessonJpaRepository.save(PtLessonJpaEntity.from(ptLesson)).toModel();
	}

	@Override
	public void saveAll(List<PtLesson> ptLessons) {
		List<PtLessonJpaEntity> ptLessonJpaEntities = ptLessons.stream()
			.map(PtLessonJpaEntity::from)
			.toList();

		ptLessonJpaRepository.saveAll(ptLessonJpaEntities);
	}

	@Override
	public List<PtLesson> findAll() {
		return ptLessonJpaRepository.findAll().stream()
			.map(PtLessonJpaEntity::toModel)
			.toList();
	}

	@Override
	public List<PtLesson> findAllByPtTrainerTrainee(PtTrainerTrainee ptTrainerTrainee) {
		return ptLessonJpaRepository.findAllByPtTrainerTraineeAndDeletedAtIsNull(
				PtTrainerTraineeJpaEntity.from(ptTrainerTrainee))
			.stream()
			.map(PtLessonJpaEntity::toModel)
			.toList();
	}

	@Override
	public List<PtLesson> findAllByPtTrainerTraineeAndIsCompletedIsFalse(PtTrainerTrainee ptTrainerTrainee) {
		return ptLessonJpaRepository.findAllByPtTrainerTraineeAndIsCompletedIsFalseAndDeletedAtIsNull(
				PtTrainerTraineeJpaEntity.from(ptTrainerTrainee))
			.stream()
			.map(PtLessonJpaEntity::toModel)
			.toList();
	}

	@Override
	public List<PtLesson> findAllByTrainerIdAndDate(Long trainerId, LocalDate date) {
		return jpaQueryFactory
			.selectFrom(ptLessonJpaEntity)
			.join(ptLessonJpaEntity.ptTrainerTrainee, ptTrainerTraineeJpaEntity)
			.join(ptTrainerTraineeJpaEntity.trainer, trainerJpaEntity)
			.join(ptTrainerTraineeJpaEntity.trainee, traineeJpaEntity)
			.join(trainerJpaEntity.member, memberJpaEntity)
			.where(
				trainerJpaEntity.id.eq(trainerId),
				ptLessonJpaEntity.lessonStart.between(date.atStartOfDay(), date.atTime(LocalTime.MAX)),
				ptTrainerTraineeJpaEntity.deletedAt.isNull(),
				trainerJpaEntity.deletedAt.isNull(),
				ptLessonJpaEntity.deletedAt.isNull(),
				memberJpaEntity.deletedAt.isNull()
			)
			.orderBy(ptLessonJpaEntity.lessonStart.asc())
			.fetch()
			.stream()
			.map(PtLessonJpaEntity::toModel)
			.toList();
	}

	@Override
	public List<PtLesson> findAllByTraineeIdForTrainerCalendar(Long traineeId, Integer year, Integer month) {
		LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
		LocalDateTime endDate = startDate.plusMonths(1).minusNanos(1);

		return jpaQueryFactory
			.selectFrom(ptLessonJpaEntity)
			.join(ptLessonJpaEntity.ptTrainerTrainee, ptTrainerTraineeJpaEntity).fetchJoin()
			.join(ptTrainerTraineeJpaEntity.trainer, trainerJpaEntity)
			.where(
				trainerJpaEntity.id.eq(traineeId),
				ptLessonJpaEntity.lessonStart.between(startDate, endDate),
				ptTrainerTraineeJpaEntity.deletedAt.isNull(),
				trainerJpaEntity.deletedAt.isNull(),
				ptLessonJpaEntity.deletedAt.isNull()
			)
			.orderBy(ptLessonJpaEntity.lessonStart.asc())
			.fetch()
			.stream()
			.map(PtLessonJpaEntity::toModel)
			.toList();
	}

	@Override
	public List<PtLesson> findAllByTraineeIdForTraineeCalendar(Long traineeId, LocalDate startDate, LocalDate endDate) {
		return jpaQueryFactory
			.selectFrom(ptLessonJpaEntity)
			.join(ptLessonJpaEntity.ptTrainerTrainee, ptTrainerTraineeJpaEntity).fetchJoin()
			.join(ptTrainerTraineeJpaEntity.trainee, traineeJpaEntity)
			.where(
				traineeJpaEntity.id.eq(traineeId),
				ptLessonJpaEntity.lessonStart.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)),
				traineeJpaEntity.deletedAt.isNull(),
				ptLessonJpaEntity.deletedAt.isNull(),
				ptTrainerTraineeJpaEntity.deletedAt.isNull()
			)
			.orderBy(ptLessonJpaEntity.lessonStart.asc())
			.fetch()
			.stream()
			.map(PtLessonJpaEntity::toModel)
			.toList();
	}

	@Override
	public Optional<PtTrainerTraineeProjection.PtInfoDto> findPtInfoByTraineeIdForDaily(Long traineeId,
		LocalDate date) {
		return Optional.ofNullable(
			jpaQueryFactory
				.select(new QPtTrainerTraineeProjection_PtInfoDto(trainerJpaEntity.member.name,
					trainerJpaEntity.member.profileImageUrl,
					ptLessonJpaEntity.session, ptLessonJpaEntity.lessonStart, ptLessonJpaEntity.lessonEnd))
				.from(ptLessonJpaEntity)
				.join(ptLessonJpaEntity.ptTrainerTrainee, ptTrainerTraineeJpaEntity)
				.join(ptTrainerTraineeJpaEntity.trainer, trainerJpaEntity)
				.join(trainerJpaEntity.member, memberJpaEntity)
				.where(ptTrainerTraineeJpaEntity.trainee.id.eq(traineeId),
					ptLessonJpaEntity.lessonStart.between(date.atStartOfDay(), date.atTime(LocalTime.MAX)),
					ptLessonJpaEntity.deletedAt.isNull(),
					ptTrainerTraineeJpaEntity.deletedAt.isNull(),
					trainerJpaEntity.deletedAt.isNull(),
					memberJpaEntity.deletedAt.isNull())
				.fetchOne());
	}

	@Override
	public PtLesson findById(Long id) {
		return Optional.ofNullable(jpaQueryFactory
				.selectFrom(ptLessonJpaEntity)
				.join(ptLessonJpaEntity.ptTrainerTrainee, ptTrainerTraineeJpaEntity).fetchJoin()
				.where(
					ptLessonJpaEntity.id.eq(id),
					ptLessonJpaEntity.deletedAt.isNull(),
					ptTrainerTraineeJpaEntity.deletedAt.isNull()
				)
				.fetchOne())
			.orElseThrow(() -> new NotFoundException(ErrorMessage.PT_LESSON_NOT_FOUND))
			.toModel();
	}

	@Override
	public boolean existsByStartAndEnd(PtTrainerTrainee pt, LocalDateTime start, LocalDateTime end) {
		return jpaQueryFactory
			.selectOne()
			.from(ptLessonJpaEntity)
			.join(ptLessonJpaEntity.ptTrainerTrainee, ptTrainerTraineeJpaEntity)
			.where(
				ptTrainerTraineeJpaEntity.trainer.id.eq(pt.getTrainer().getId()),
				ptLessonJpaEntity.lessonStart.lt(end),
				ptLessonJpaEntity.lessonEnd.gt(start),
				ptLessonJpaEntity.deletedAt.isNull(),
				ptTrainerTraineeJpaEntity.deletedAt.isNull()
			)
			.fetchFirst() != null;
	}

	@Override
	public boolean existsByStart(PtTrainerTrainee pt, LocalDateTime start) {
		return jpaQueryFactory
			.selectOne()
			.from(ptLessonJpaEntity)
			.join(ptLessonJpaEntity.ptTrainerTrainee, ptTrainerTraineeJpaEntity)
			.where(
				ptTrainerTraineeJpaEntity.trainer.id.eq(pt.getTrainer().getId()),
				ptTrainerTraineeJpaEntity.trainee.id.eq(pt.getTrainee().getId()),
				ptLessonJpaEntity.lessonStart.year().eq(start.getYear())
					.and(ptLessonJpaEntity.lessonStart.month().eq(start.getMonthValue()))
					.and(ptLessonJpaEntity.lessonStart.dayOfMonth().eq(start.getDayOfMonth())),
				ptLessonJpaEntity.deletedAt.isNull(),
				ptTrainerTraineeJpaEntity.deletedAt.isNull()
			)
			.fetchFirst() != null;
	}
}
