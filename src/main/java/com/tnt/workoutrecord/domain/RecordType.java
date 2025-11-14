package com.tnt.workoutrecord.domain;

import static com.tnt.common.error.model.ErrorMessage.UNSUPPORTED_RECORD_TYPE;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.tnt.common.error.exception.TnTException;

public enum RecordType {
	PT,
	TRAINEE;

	@JsonCreator
	public static RecordType of(String value) {
		for (RecordType type : RecordType.values()) {
			if (type.name().equalsIgnoreCase(value)) {
				return type;
			}
		}

		throw new TnTException(UNSUPPORTED_RECORD_TYPE);
	}
}
