package com.tnt.pt.infrastructure;

import static com.tnt.member.infrastructure.QMemberJpaEntity.memberJpaEntity;
import static com.tnt.pt.infrastructure.QPtTrainerTraineeJpaEntity.ptTrainerTraineeJpaEntity;
import static com.tnt.trainee.infrastructure.QTraineeJpaEntity.traineeJpaEntity;
import static com.tnt.trainer.infrastructure.QTrainerJpaEntity.trainerJpaEntity;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tnt.common.error.exception.NotFoundException;
import com.tnt.common.error.model.ErrorMessage;
import com.tnt.pt.application.repository.PtTrainerTraineeRepository;
import com.tnt.pt.domain.PtTrainerTrainee;
import com.tnt.trainee.domain.Trainee;
import com.tnt.trainee.infrastructure.TraineeJpaEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PtTrainerTraineeRepositoryImpl implements PtTrainerTraineeRepository {

	private final PtTrainerTraineeJpaRepository ptTrainerTraineeJpaRepository;
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public PtTrainerTrainee save(PtTrainerTrainee ptTrainerTrainee) {
		return ptTrainerTraineeJpaRepository.save(PtTrainerTraineeJpaEntity.from(ptTrainerTrainee)).toModel();
	}

	@Override
	public void saveAll(List<PtTrainerTrainee> ptTrainerTrainees) {
		List<PtTrainerTraineeJpaEntity> ptTrainerTraineeJpaEntities = ptTrainerTrainees.stream()
			.map(PtTrainerTraineeJpaEntity::from)
			.toList();

		ptTrainerTraineeJpaRepository.saveAll(ptTrainerTraineeJpaEntities);
	}

	@Override
	public PtTrainerTrainee findByTrainerId(Long trainerId) {
		return ptTrainerTraineeJpaRepository.findByTrainerIdAndDeletedAtIsNull(trainerId)
			.orElseThrow(() -> new NotFoundException(ErrorMessage.PT_TRAINER_TRAINEE_NOT_FOUND)).toModel();
	}

	@Override
	public PtTrainerTrainee findByTraineeId(Long traineeId) {
		return ptTrainerTraineeJpaRepository.findByTraineeIdAndDeletedAtIsNull(traineeId)
			.orElseThrow(() -> new NotFoundException(ErrorMessage.PT_TRAINER_TRAINEE_NOT_FOUND)).toModel();
	}

	@Override
	public List<PtTrainerTrainee> findAllByTrainerId(Long trainerId) {
		List<PtTrainerTraineeJpaEntity> jpaEntities =
			ptTrainerTraineeJpaRepository.findAllByTrainerIdAndDeletedAtIsNull(trainerId);

		return jpaEntities.stream()
			.map(PtTrainerTraineeJpaEntity::toModel)
			.toList();
	}

	@Override
	public List<PtTrainerTrainee> findAllByTrainerIdWithDeleted(Long trainerId) {
		List<PtTrainerTraineeJpaEntity> jpaEntities = ptTrainerTraineeJpaRepository.findAllByTrainerId(trainerId);

		return jpaEntities.stream()
			.map(PtTrainerTraineeJpaEntity::toModel)
			.toList();
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
		List<TraineeJpaEntity> traineeJpaEntities = jpaQueryFactory
			.select(ptTrainerTraineeJpaEntity.trainee)
			.from(ptTrainerTraineeJpaEntity)
			.join(ptTrainerTraineeJpaEntity.trainer, trainerJpaEntity)
			.join(ptTrainerTraineeJpaEntity.trainee, traineeJpaEntity)
			.join(traineeJpaEntity.member, memberJpaEntity)
			.where(
				ptTrainerTraineeJpaEntity.trainer.id.eq(trainerId),
				ptTrainerTraineeJpaEntity.deletedAt.isNull(),
				memberJpaEntity.deletedAt.isNull(),
				traineeJpaEntity.deletedAt.isNull(),
				trainerJpaEntity.deletedAt.isNull()
			)
			.fetch();

		return traineeJpaEntities.stream().map(TraineeJpaEntity::toModel).toList();
	}
}
