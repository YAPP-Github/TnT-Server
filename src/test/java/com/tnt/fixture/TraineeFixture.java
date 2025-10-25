package com.tnt.fixture;

import static com.tnt.trainee.domain.PtGoal.STRENGTH_ENHANCE;
import static com.tnt.trainee.domain.PtGoal.WEIGHT_LOSS;

import java.util.Arrays;
import java.util.List;

import com.tnt.member.domain.Member;
import com.tnt.trainee.domain.PtGoal;
import com.tnt.trainee.domain.Trainee;

public final class TraineeFixture {

	static List<PtGoal> ptGoals = Arrays.asList(WEIGHT_LOSS, STRENGTH_ENHANCE);

	public static Trainee getTrainee1WithId(Member member) {
		Long traineeId = 1L;

		return Trainee.builder()
			.id(traineeId)
			.member(member)
			.height(180.4)
			.weight(70.5)
			.cautionNote("주의사항00")
			.ptGoals(ptGoals)
			.build();
	}

	public static Trainee getTrainee1(Member member) {
		return Trainee.builder()
			.member(member)
			.height(170.5)
			.weight(60.5)
			.cautionNote("주의사항11")
			.ptGoals(ptGoals)
			.build();
	}

	public static Trainee getTrainee2(Member member) {
		return Trainee.builder()
			.member(member)
			.height(173.3)
			.weight(65.5)
			.cautionNote("주의사항22")
			.ptGoals(ptGoals)
			.build();
	}
}
