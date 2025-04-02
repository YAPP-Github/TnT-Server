package com.tnt.pt.infrastructure;

import static com.tnt.member.domain.QMember.member;
import static com.tnt.pt.domain.QPtTrainerTrainee.ptTrainerTrainee;
import static com.tnt.trainee.domain.QTrainee.trainee;
import static com.tnt.trainer.domain.QTrainer.trainer;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tnt.common.error.exception.NotFoundException;
import com.tnt.common.error.model.ErrorMessage;
import com.tnt.pt.application.repository.PtTrainerTraineeRepository;
import com.tnt.pt.domain.PtTrainerTrainee;
import com.tnt.trainee.domain.Trainee;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PtTrainerTraineeRepositoryImpl implements PtTrainerTraineeRepository {

	private final PtTrainerTraineeJpaRepository ptTrainerTraineeJpaRepository;
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public PtTrainerTrainee save(PtTrainerTrainee ptTrainerTrainee) {
		return ptTrainerTraineeJpaRepository.save(ptTrainerTrainee);
	}

	@Override
	public void saveAll(List<PtTrainerTrainee> ptTrainerTrainees) {
		ptTrainerTraineeJpaRepository.saveAll(ptTrainerTrainees);
	}

	@Override
	public PtTrainerTrainee findByTrainerId(Long trainerId) {
		return ptTrainerTraineeJpaRepository.findByTrainerIdAndDeletedAtIsNull(trainerId)
			.orElseThrow(() -> new NotFoundException(ErrorMessage.PT_TRAINER_TRAINEE_NOT_FOUND));
	}

	@Override
	public PtTrainerTrainee findByTraineeId(Long traineeId) {
		return ptTrainerTraineeJpaRepository.findByTraineeIdAndDeletedAtIsNull(traineeId)
			.orElseThrow(() -> new NotFoundException(ErrorMessage.PT_TRAINER_TRAINEE_NOT_FOUND));
	}

	@Override
	public List<PtTrainerTrainee> findAllByTrainerId(Long trainerId) {
		return ptTrainerTraineeJpaRepository.findAllByTrainerIdAndDeletedAtIsNull(trainerId);
	}

	@Override
	public List<PtTrainerTrainee> findAllByTrainerIdWithDeleted(Long trainerId) {
		return ptTrainerTraineeJpaRepository.findAllByTrainerId(trainerId);
	}

	@Override
	public boolean existsByTrainerId(Long trainerId) {
		return ptTrainerTraineeJpaRepository.existsByTrainerIdAndDeletedAtIsNull(trainerId);
	}

	@Override
	public boolean existsByTraineeId(Long traineeId) {
		return ptTrainerTraineeJpaRepository.existsByTraineeIdAndDeletedAtIsNull(traineeId);
	}

	@Override
	public boolean existsByTrainerIdAndTraineeId(Long trainerId, Long traineeId) {
		return ptTrainerTraineeJpaRepository.existsByTrainerIdAndTraineeIdAndDeletedAtIsNull(trainerId, traineeId);
	}

	@Override
	public List<Trainee> findAllTrainees(Long trainerId) {
		return jpaQueryFactory
			.select(ptTrainerTrainee.trainee)
			.from(ptTrainerTrainee)
			.join(ptTrainerTrainee.trainer, trainer)
			.join(ptTrainerTrainee.trainee, trainee)
			.join(trainee.member, member)
			.where(
				ptTrainerTrainee.trainer.id.eq(trainerId),
				ptTrainerTrainee.deletedAt.isNull(),
				member.deletedAt.isNull(),
				trainee.deletedAt.isNull(),
				trainer.deletedAt.isNull()
			)
			.fetch();
	}
}
