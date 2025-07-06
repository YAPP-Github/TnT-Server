package com.tnt.trainee.domain;

import static com.tnt.common.error.model.ErrorMessage.UNSUPPORTED_PT_GOAL;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.tnt.common.error.exception.TnTException;

public enum PtGoal {
	WEIGHT_LOSS,
	STRENGTH_ENHANCE,
	HEALTH_MANAGE,
	FLEXIBILITY_ENHANCE,
	BODY_PROFILE,
	POSTURE_CORRECTION;

	@JsonCreator
	public static PtGoal of(String value) {
		for (PtGoal type : PtGoal.values()) {
			if (type.name().equalsIgnoreCase(value)) { // 대소문자 구분 없이 처리
				return type;
			}
		}
		throw new TnTException(UNSUPPORTED_PT_GOAL);
	}
}
