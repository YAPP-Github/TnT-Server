package com.tnt.trainee.infrastructure;

import static com.tnt.member.infrastructure.QMemberJpaEntity.memberJpaEntity;
import static com.tnt.trainee.infrastructure.QTraineeJpaEntity.traineeJpaEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tnt.common.error.exception.NotFoundException;
import com.tnt.common.error.model.ErrorMessage;
import com.tnt.common.jpa.DynamicQuery;
import com.tnt.trainee.application.repository.TraineeRepository;
import com.tnt.trainee.domain.Trainee;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TraineeRepositoryImpl implements TraineeRepository {

	private final TraineeJpaRepository traineeJpaRepository;
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Trainee save(Trainee trainee) {
		return traineeJpaRepository.save(TraineeJpaEntity.from(trainee)).toModel();
	}

	@Override
	public Trainee findByMemberId(Long memberId) {
		return traineeJpaRepository.findByMemberIdAndDeletedAtIsNull(memberId)
			.orElseThrow(() -> new NotFoundException(ErrorMessage.TRAINEE_NOT_FOUND)).toModel();
	}

	@Override
	public List<Trainee> findAll() {
		return traineeJpaRepository.findAll().stream()
			.map(TraineeJpaEntity::toModel)
			.toList();
	}

	@Override
	public Trainee find(@Nullable Long memberId, @Nullable Long traineeId) {
		return Optional.ofNullable(jpaQueryFactory
				.selectFrom(traineeJpaEntity)
				.join(traineeJpaEntity.member, memberJpaEntity).fetchJoin()
				.where(
					DynamicQuery.generateEq(memberId, memberJpaEntity.id::eq),
					DynamicQuery.generateEq(traineeId, traineeJpaEntity.id::eq),
					memberJpaEntity.deletedAt.isNull(),
					traineeJpaEntity.deletedAt.isNull()
				)
				.fetchOne())
			.orElseThrow(() -> new NotFoundException(ErrorMessage.TRAINEE_NOT_FOUND))
			.toModel();
	}
}
