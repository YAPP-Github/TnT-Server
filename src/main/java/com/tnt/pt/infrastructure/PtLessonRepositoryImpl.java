package com.tnt.pt.infrastructure;

import static com.tnt.member.domain.QMember.member;
import static com.tnt.pt.domain.QPtLesson.ptLesson;
import static com.tnt.pt.domain.QPtTrainerTrainee.ptTrainerTrainee;
import static com.tnt.trainee.domain.QTrainee.trainee;
import static com.tnt.trainer.domain.QTrainer.trainer;

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
		return ptLessonJpaRepository.save(ptLesson);
	}

	@Override
	public void saveAll(List<PtLesson> ptLessons) {
		ptLessonJpaRepository.saveAll(ptLessons);
	}

	@Override
	public List<PtLesson> findAll() {
		return ptLessonJpaRepository.findAll();
	}

	@Override
	public List<PtLesson> findAllByPtTrainerTrainee(PtTrainerTrainee ptTrainerTrainee) {
		return ptLessonJpaRepository.findAllByPtTrainerTraineeAndDeletedAtIsNull(ptTrainerTrainee);
	}

	@Override
	public List<PtLesson> findAllByPtTrainerTraineeAndIsCompletedIsFalse(PtTrainerTrainee ptTrainerTrainee) {
		return ptLessonJpaRepository.findAllByPtTrainerTraineeAndIsCompletedIsFalseAndDeletedAtIsNull(ptTrainerTrainee);
	}

	@Override
	public List<PtLesson> findAllByTrainerIdAndDate(Long trainerId, LocalDate date) {
		return jpaQueryFactory
			.selectFrom(ptLesson)
			.join(ptLesson.ptTrainerTrainee, ptTrainerTrainee).fetchJoin()
			.join(ptTrainerTrainee.trainer, trainer).fetchJoin()
			.join(ptTrainerTrainee.trainee, trainee).fetchJoin()
			.join(trainee.member, member).fetchJoin()
			.where(
				trainer.id.eq(trainerId),
				ptLesson.lessonStart.between(date.atStartOfDay(), date.atTime(LocalTime.MAX)),
				ptTrainerTrainee.deletedAt.isNull(),
				trainer.deletedAt.isNull(),
				ptLesson.deletedAt.isNull(),
				member.deletedAt.isNull()
			)
			.orderBy(ptLesson.lessonStart.asc())
			.fetch();
	}

	@Override
	public List<PtLesson> findAllByTraineeIdForTrainerCalendar(Long traineeId, Integer year, Integer month) {
		LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
		LocalDateTime endDate = startDate.plusMonths(1).minusNanos(1);

		return jpaQueryFactory
			.selectFrom(ptLesson)
			.join(ptLesson.ptTrainerTrainee, ptTrainerTrainee).fetchJoin()
			.join(ptTrainerTrainee.trainer, trainer).fetchJoin()
			.where(
				trainer.id.eq(traineeId),
				ptLesson.lessonStart.between(startDate, endDate),
				ptTrainerTrainee.deletedAt.isNull(),
				trainer.deletedAt.isNull(),
				ptLesson.deletedAt.isNull()
			)
			.orderBy(ptLesson.lessonStart.asc())
			.fetch();
	}

	@Override
	public List<PtLesson> findAllByTraineeIdForTraineeCalendar(Long traineeId, LocalDate startDate, LocalDate endDate) {
		return jpaQueryFactory
			.selectFrom(ptLesson)
			.join(ptLesson.ptTrainerTrainee, ptTrainerTrainee).fetchJoin()
			.join(ptTrainerTrainee.trainee, trainee).fetchJoin()
			.where(
				trainee.id.eq(traineeId),
				ptLesson.lessonStart.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)),
				trainee.deletedAt.isNull(),
				ptLesson.deletedAt.isNull(),
				ptTrainerTrainee.deletedAt.isNull()
			)
			.orderBy(ptLesson.lessonStart.asc())
			.fetch();
	}

	@Override
	public Optional<PtTrainerTraineeProjection.PtInfoDto> findPtInfoByTraineeIdForDaily(Long traineeId,
		LocalDate date) {
		return Optional.ofNullable(
			jpaQueryFactory
				.select(new QPtTrainerTraineeProjection_PtInfoDto(trainer.member.name, trainer.member.profileImageUrl,
					ptLesson.session, ptLesson.lessonStart, ptLesson.lessonEnd))
				.from(ptLesson)
				.join(ptLesson.ptTrainerTrainee, ptTrainerTrainee)
				.join(ptTrainerTrainee.trainer, trainer)
				.join(trainer.member, member)
				.where(ptTrainerTrainee.trainee.id.eq(traineeId),
					ptLesson.lessonStart.between(date.atStartOfDay(), date.atTime(LocalTime.MAX)),
					ptLesson.deletedAt.isNull(),
					ptTrainerTrainee.deletedAt.isNull(),
					trainer.deletedAt.isNull(),
					member.deletedAt.isNull())
				.fetchOne());
	}

	@Override
	public PtLesson findById(Long id) {
		return Optional.ofNullable(jpaQueryFactory
				.selectFrom(ptLesson)
				.join(ptLesson.ptTrainerTrainee, ptTrainerTrainee).fetchJoin()
				.where(
					ptLesson.id.eq(id),
					ptLesson.deletedAt.isNull(),
					ptTrainerTrainee.deletedAt.isNull()
				)
				.fetchOne())
			.orElseThrow(() -> new NotFoundException(ErrorMessage.PT_LESSON_NOT_FOUND));
	}

	@Override
	public boolean existsByStartAndEnd(PtTrainerTrainee pt, LocalDateTime start, LocalDateTime end) {
		return jpaQueryFactory
			.selectOne()
			.from(ptLesson)
			.join(ptLesson.ptTrainerTrainee, ptTrainerTrainee)
			.where(
				ptTrainerTrainee.trainer.id.eq(pt.getTrainer().getId()),
				ptLesson.lessonStart.lt(end),
				ptLesson.lessonEnd.gt(start),
				ptLesson.deletedAt.isNull(),
				ptTrainerTrainee.deletedAt.isNull()
			)
			.fetchFirst() != null;
	}

	@Override
	public boolean existsByStart(PtTrainerTrainee pt, LocalDateTime start) {
		return jpaQueryFactory
			.selectOne()
			.from(ptLesson)
			.join(ptLesson.ptTrainerTrainee, ptTrainerTrainee)
			.where(
				ptTrainerTrainee.trainer.id.eq(pt.getTrainer().getId()),
				ptTrainerTrainee.trainee.id.eq(pt.getTrainee().getId()),
				ptLesson.lessonStart.year().eq(start.getYear())
					.and(ptLesson.lessonStart.month().eq(start.getMonthValue()))
					.and(ptLesson.lessonStart.dayOfMonth().eq(start.getDayOfMonth())),
				ptLesson.deletedAt.isNull(),
				ptTrainerTrainee.deletedAt.isNull()
			)
			.fetchFirst() != null;
	}
}
