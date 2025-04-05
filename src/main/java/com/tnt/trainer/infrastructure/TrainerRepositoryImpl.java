package com.tnt.trainer.infrastructure;

import static com.tnt.common.error.model.ErrorMessage.TRAINER_NOT_FOUND;
import static com.tnt.member.infrastructure.QMemberJpaEntity.memberJpaEntity;
import static com.tnt.trainer.infrastructure.QTrainerJpaEntity.trainerJpaEntity;

import java.util.Optional;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tnt.common.error.exception.NotFoundException;
import com.tnt.common.jpa.DynamicQuery;
import com.tnt.trainer.application.repository.TrainerRepository;
import com.tnt.trainer.domain.Trainer;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TrainerRepositoryImpl implements TrainerRepository {

	private final TrainerJpaRepository trainerJpaRepository;
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Trainer save(Trainer trainer) {
		return trainerJpaRepository.save(TrainerJpaEntity.from(trainer)).toModel();
	}

	@Override
	public Trainer findByMemberId(Long memberId) {
		return trainerJpaRepository.findByMemberIdAndDeletedAtIsNull(memberId)
			.orElseThrow(() -> new NotFoundException(TRAINER_NOT_FOUND)).toModel();
	}

	@Override
	public boolean existsByMemberId(Long memberId) {
		return trainerJpaRepository.existsByMemberIdAndDeletedAtIsNull(memberId);
	}

	@Override
	public boolean existsByInvitationCode(String invitationCode) {
		return trainerJpaRepository.existsByInvitationCodeAndDeletedAtIsNull(invitationCode);
	}

	public Trainer find(@Nullable Long memberId, @Nullable String invitationCode) {
		return Optional.ofNullable(jpaQueryFactory
				.selectFrom(trainerJpaEntity)
				.join(trainerJpaEntity.member, memberJpaEntity).fetchJoin()
				.where(
					DynamicQuery.generateEq(memberId, memberJpaEntity.id::eq),
					DynamicQuery.generateEq(invitationCode, trainerJpaEntity.invitationCode::eq),
					memberJpaEntity.deletedAt.isNull(),
					trainerJpaEntity.deletedAt.isNull()
				)
				.fetchOne())
			.orElseThrow(() -> new NotFoundException(TRAINER_NOT_FOUND))
			.toModel();
	}
}
