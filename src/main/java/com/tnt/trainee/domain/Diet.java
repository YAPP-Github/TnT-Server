package com.tnt.trainee.domain;

import static com.tnt.common.error.model.ErrorMessage.DIET_INVALID_IMAGE_URL;
import static com.tnt.common.error.model.ErrorMessage.DIET_INVALID_MEMO;
import static io.micrometer.common.util.StringUtils.isBlank;
import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Diet {

	public static final int DIET_IMAGE_URL_LENGTH = 255;
	public static final int MEMO_LENGTH = 100;

	private final Long id;
	private final Long traineeId;
	private final LocalDateTime date;
	private final String dietImageUrl;
	private final String memo;
	private final DietType dietType;
	private LocalDateTime deletedAt;

	@Builder
	public Diet(Long id, Long traineeId, LocalDateTime date, String dietImageUrl, String memo,
		DietType dietType, LocalDateTime deletedAt) {
		this.id = id;
		this.traineeId = requireNonNull(traineeId);
		this.date = requireNonNull(date);
		this.dietImageUrl = validateDietImageUrl(dietImageUrl);
		this.memo = validateMemo(memo);
		this.dietType = requireNonNull(dietType);
		this.deletedAt = deletedAt;
	}

	public void softDelete() {
		this.deletedAt = LocalDateTime.now();
	}

	private String validateDietImageUrl(String dietImageUrl) {
		if (!isBlank(dietImageUrl) && dietImageUrl.length() > DIET_IMAGE_URL_LENGTH) {
			throw new IllegalArgumentException(DIET_INVALID_IMAGE_URL.getMessage());
		}

		return dietImageUrl;
	}

	private String validateMemo(String memo) {
		if (isBlank(memo) || memo.length() > MEMO_LENGTH) {
			throw new IllegalArgumentException(DIET_INVALID_MEMO.getMessage());
		}

		return memo;
	}
}
