package com.tnt.trainer.dto;

import com.tnt.trainee.dto.response.ConnectWithTrainerResponse;

public record ConnectWithTrainerDto(
	String trainerFcmToken,
	String trainerName,
	String traineeName,
	String trainerProfileImageUrl,
	String traineeProfileImageUrl,
	Long trainerId,
	Long traineeId
) {

	public ConnectWithTrainerResponse toResponse() {
		return new ConnectWithTrainerResponse(trainerName, traineeName, trainerProfileImageUrl, traineeProfileImageUrl);
	}
}
