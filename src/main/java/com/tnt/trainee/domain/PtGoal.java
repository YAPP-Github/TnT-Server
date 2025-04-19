package com.tnt.trainee.domain;

import static com.tnt.common.error.model.ErrorMessage.PT_GOAL_INVALID_CONTENT;
import static io.micrometer.common.util.StringUtils.isBlank;
import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PtGoal {

	public static final int CONTENT_LENGTH = 100;

	private final Long id;
	private final Long traineeId;
	private final String content;
	private LocalDateTime deletedAt;

	@Builder
	public PtGoal(Long id, Long traineeId, String content, LocalDateTime deletedAt) {
		this.id = id;
		this.traineeId = requireNonNull(traineeId);
		this.content = validateContent(content);
		this.deletedAt = deletedAt;
	}

	private String validateContent(String content) {
		if (isBlank(content) || content.length() > CONTENT_LENGTH) {
			throw new IllegalArgumentException(PT_GOAL_INVALID_CONTENT.getMessage());
		}

		return content;
	}

	public void softDelete() {
		this.deletedAt = LocalDateTime.now();
	}
}
