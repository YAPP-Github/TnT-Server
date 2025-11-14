package com.tnt.workoutrecord.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "운동 기록 수정 요청")
public record UpdateWorkoutRecordRequest(
	@Schema(description = "피드백", example = "수정된 피드백입니다.", nullable = true)
	String feedback,

	@Schema(description = "삭제할 이미지 URL 리스트", nullable = true)
	List<String> imageUrlsToDelete,

	@Schema(description = "루틴 정보 리스트", nullable = false)
	@Valid
	@Size(min = 1, message = "최소 1개 이상의 루틴이 필요합니다.")
	@NotNull(message = "루틴 정보는 필수입니다.")
	List<RoutineInfo> routines
) {
	@Schema(description = "루틴 정보")
	public record RoutineInfo(
		@Schema(description = "운동 ID", example = "10", nullable = false)
		@NotNull(message = "운동 ID는 필수입니다.")
		Long workoutId,

		@Schema(description = "세트 정보 리스트", nullable = false)
		@Valid
		@Size(min = 1, message = "최소 1개 이상의 세트가 필요합니다.")
		@NotNull(message = "세트 정보는 필수입니다.")
		List<SetInfo> sets
	) {
	}

	@Schema(description = "세트 정보")
	public record SetInfo(
		@Schema(description = "지속 시간 (분, 유산소 운동용)", example = "30", nullable = true)
		Integer durationMinutes,

		@Schema(description = "반복 횟수 (무산소 운동용)", example = "12", nullable = true)
		Integer repetition,

		@Schema(description = "무게 (kg)", example = "50", nullable = true)
		Integer weight
	) {
	}
}
