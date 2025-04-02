package com.tnt.pt.dto;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PtTrainerTraineeProjection {

	@QueryProjection
	public record PtInfoDto(
		String trainerName,
		String trainerProfileImage,
		Integer session,
		LocalDateTime lessonStart,
		LocalDateTime lessonEnd
	) {

	}
}
