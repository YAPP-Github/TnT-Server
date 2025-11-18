package com.tnt.workout.domain;

import static com.tnt.common.error.model.ErrorMessage.TRAINER_INVALID_INVITATION_CODE;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Workout {

	public static final int NAME_LENGTH = 20;

	private Long id;
	private String name;
	private String imageUrl;
	private List<BodyPart> bodyParts;
	private List<Machine> machines;
	private WorkoutType workoutType;

	@Builder
	public Workout(Long id, String name, String imageUrl, List<BodyPart> bodyParts, List<Machine> machines,
		WorkoutType workoutType) {
		this.id = id;
		this.name = validateName(name);
		this.imageUrl = imageUrl;
		this.bodyParts = bodyParts;
		this.machines = machines;
		this.workoutType = workoutType;
	}

	private String validateName(String name) {
		if (name == null || name.isBlank() || name.length() > NAME_LENGTH) {
			throw new IllegalArgumentException(TRAINER_INVALID_INVITATION_CODE.getMessage());
		}

		return name;
	}
}
