package com.tnt.fixture;

import com.tnt.domain.member.Member;
import com.tnt.domain.trainer.Trainer;

public final class TrainerFixture {

	public static Trainer getTrainer1(Member member) {
		return Trainer.builder()
			.member(member)
			.build();
	}

	public static Trainer getTrainerWithId1(Member member) {
		Long trainerId = 1L;

		return Trainer.builder()
			.id(trainerId)
			.member(member)
			.build();
	}
}
