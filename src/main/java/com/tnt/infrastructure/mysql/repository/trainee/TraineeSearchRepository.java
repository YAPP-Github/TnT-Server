package com.tnt.infrastructure.mysql.repository.trainee;

import static com.tnt.domain.member.QMember.member;
import static com.tnt.domain.trainee.QTrainee.trainee;

import java.util.Optional;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tnt.domain.trainee.Trainee;
import com.tnt.infrastructure.mysql.DynamicQuery;

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
