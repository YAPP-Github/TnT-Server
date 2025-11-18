package com.tnt.workout.dto.response;

import java.util.List;

import com.tnt.workout.domain.BodyPart;
import com.tnt.workout.domain.Machine;
import com.tnt.workout.domain.Workout;
import com.tnt.workout.domain.WorkoutType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "운동 검색 응답")
public record SearchWorkoutResponse(
	@Schema(description = "운동 목록")
	List<WorkoutInfo> workouts,

	@Schema(description = "다음 페이지 존재 여부", example = "true")
	boolean hasNext
) {

	public static SearchWorkoutResponse from(List<Workout> workouts, boolean hasNext) {
		List<WorkoutInfo> workoutInfos = workouts.stream()
			.map(WorkoutInfo::from)
			.toList();
		return new SearchWorkoutResponse(workoutInfos, hasNext);
	}

	@Schema(description = "운동 정보")
	public record WorkoutInfo(
		@Schema(description = "운동 ID", example = "1")
		Long workoutId,

		@Schema(description = "운동 이름", example = "벤치프레스")
		String name,

		@Schema(description = "운동 이미지 URL", example = "https://images.tntapp.co.kr/bench-press.jpg")
		String imageUrl,

		@Schema(description = "운동 부위 목록", example = "[\"CHEST\", \"TRICEPS\"]")
		List<BodyPart> bodyParts,

		@Schema(description = "운동 기구 목록", example = "[\"BARBELL\"]")
		List<Machine> machines,

		@Schema(description = "운동 타입", example = "ANAEROBIC")
		WorkoutType workoutType
	) {

		public static WorkoutInfo from(Workout workout) {
			return new WorkoutInfo(
				workout.getId(),
				workout.getName(),
				workout.getImageUrl(),
				workout.getBodyParts(),
				workout.getMachines(),
				workout.getWorkoutType()
			);
		}
	}
}
