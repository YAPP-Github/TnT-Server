package com.tnt.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageConstant {

	public static final String TRAINER_DEFAULT_IMAGE = "https://images.tntapp.co.kr/profiles/trainers/basic_trainer_image.png";
	public static final String TRAINEE_DEFAULT_IMAGE = "https://images.tntapp.co.kr/profiles/trainees/basic_trainee_image.png";

	public static final String TRAINER_S3_PROFILE_IMAGE_PATH = "profiles/trainers";
	public static final String TRAINEE_S3_PROFILE_IMAGE_PATH = "profiles/trainees";

	public static final String WORKOUT_AEROBIC_S3_IMAGE_PATH = "workouts/aerobic";
	public static final String WORKOUT_ANAEROBIC_S3_IMAGE_PATH = "workouts/anaerobic";
	public static final String WORKOUT_RECORD_S3_IMAGE_PATH = "workout-records/";

	public static final String DIET_S3_IMAGE_PATH = "diets/trainees";
}
