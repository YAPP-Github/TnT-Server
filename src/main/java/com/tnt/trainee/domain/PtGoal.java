package com.tnt.trainee.domain;

import static com.tnt.common.error.model.ErrorMessage.UNSUPPORTED_PT_GOAL;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.tnt.common.error.exception.TnTException;

public enum PtGoal {
	WEIGHT_LOSS("체중 감량"),
	STRENGTH_ENHANCE("근력 향상"),
	HEALTH_MANAGE("건강 관리"),
	FLEXIBILITY_ENHANCE("유연성 향상"),
	BODY_PROFILE("바디프로필"),
	POSTURE_CORRECTION("자세 교정");

	private final String koreanName;

	PtGoal(String koreanName) {
		this.koreanName = koreanName;
	}

	@JsonCreator
	public static PtGoal of(String value) {
		// 1. 영어 enum 이름으로 시도
		for (PtGoal goal : PtGoal.values()) {
			if (goal.name().equalsIgnoreCase(value)) {
				return goal;
			}
		}

		// 2. 한글 이름으로 시도
		for (PtGoal goal : PtGoal.values()) {
			if (goal.koreanName.equals(value)) {
				return goal;
			}
		}

		throw new TnTException(UNSUPPORTED_PT_GOAL);
	}
}
