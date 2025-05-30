package com.tnt.trainee.domain;

import static com.tnt.common.error.model.ErrorMessage.PT_GOAL_INVALID_CONTENT;
import static io.micrometer.common.util.StringUtils.isBlank;
import static java.util.Objects.requireNonNull;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PtGoal {

	public static final int CONTENT_LENGTH = 100;

	private final Long id;
	private final Long traineeId;
	private final String content;

	@Builder
	public PtGoal(Long id, Long traineeId, String content) {
		this.id = id;
		this.traineeId = requireNonNull(traineeId);
		this.content = validateContent(content);
	}

	private String validateContent(String content) {
		if (isBlank(content) || content.length() > CONTENT_LENGTH) {
			throw new IllegalArgumentException(PT_GOAL_INVALID_CONTENT.getMessage());
		}

		return content;
	}
}
