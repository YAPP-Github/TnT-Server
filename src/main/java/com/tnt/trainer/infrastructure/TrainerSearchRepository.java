package com.tnt.trainer.infrastructure;

import static com.tnt.member.domain.QMember.member;
import static com.tnt.trainer.domain.QTrainer.trainer;

import java.util.Optional;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tnt.common.jpa.DynamicQuery;
import com.tnt.trainer.domain.Trainer;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TrainerSearchRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Optional<Trainer> find(@Nullable Long memberId, @Nullable String invitationCode) {
		return Optional.ofNullable(jpaQueryFactory
			.selectFrom(trainer)
			.join(trainer.member, member).fetchJoin()
			.where(
				DynamicQuery.generateEq(memberId, member.id::eq),
				DynamicQuery.generateEq(invitationCode, trainer.invitationCode::eq),
				member.deletedAt.isNull(),
				trainer.deletedAt.isNull()
			)
			.fetchOne());
	}
}
