package com.tnt.pt.infrastructure;

import static com.tnt.member.domain.QMember.member;
import static com.tnt.pt.domain.QPtTrainerTrainee.ptTrainerTrainee;
import static com.tnt.trainee.domain.QTrainee.trainee;
import static com.tnt.trainer.domain.QTrainer.trainer;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tnt.trainee.domain.Trainee;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PtTrainerTraineeSearchRepository {

	private final JPAQueryFactory jpaQueryFactory;

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
