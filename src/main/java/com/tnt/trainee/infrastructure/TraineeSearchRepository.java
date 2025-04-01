package com.tnt.trainee.infrastructure;

import static com.tnt.member.domain.QMember.member;
import static com.tnt.trainee.domain.QTrainee.trainee;

import java.util.Optional;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tnt.common.jpa.DynamicQuery;
import com.tnt.trainee.domain.Trainee;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TraineeSearchRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Optional<Trainee> find(@Nullable Long memberId, @Nullable Long traineeId) {
		return Optional.ofNullable(jpaQueryFactory
			.selectFrom(trainee)
			.join(trainee.member, member).fetchJoin()
			.where(
				DynamicQuery.generateEq(memberId, member.id::eq),
				DynamicQuery.generateEq(traineeId, trainee.id::eq),
				member.deletedAt.isNull(),
				trainee.deletedAt.isNull()
			)
			.fetchOne());
	}
}
