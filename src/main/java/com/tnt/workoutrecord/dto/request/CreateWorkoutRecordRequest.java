package com.tnt.workoutrecord.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.tnt.workoutrecord.domain.RecordType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "운동 기록 등록 요청")
public record CreateWorkoutRecordRequest(
	@Schema(description = "PT 수업 ID (PT 기록인 경우)", example = "123456789", nullable = true)
	Long ptLessonId,

	@Schema(description = "운동한 날짜 및 시간", example = "2025-01-15T14:30:00", nullable = false)
	@NotNull
	LocalDateTime date,

	@Schema(description = "기록 타입 (PT, TRAINEE)", example = "TRAINEE", nullable = false)
	@NotNull
	RecordType recordType,

	@Schema(description = "운동 루틴 목록", nullable = false)
	@Valid
	@Size(min = 1, message = "최소 1개 이상의 루틴이 필요합니다.")
	@NotNull(message = "루틴 정보는 필수입니다.")
	List<RoutineInfo> routines,

	@Schema(description = "피드백 내용", example = "오늘 운동 강도가 적절했습니다.", nullable = true)
	String feedback
) {

	@Schema(description = "운동 루틴 정보")
	public record RoutineInfo(
		@Schema(description = "운동 ID", example = "1", nullable = false)
		@NotNull
		Long workoutId,

		@Schema(description = "세트 목록", nullable = false)
		@Valid
		@Size(min = 1, message = "최소 1개 이상의 세트가 필요합니다.")
		@NotNull(message = "세트 정보는 필수입니다.")
		List<SetInfo> sets
	) {

	}

	@Schema(description = "운동 세트 정보")
	public record SetInfo(
		@Schema(description = "운동 시간 (분 단위, 유산소 운동용)", example = "30", nullable = true)
		Integer durationMinutes,

		@Schema(description = "반복 횟수 (무산소 운동용)", example = "12", nullable = true)
		Integer repetition,

		@Schema(description = "무게 (kg, 무산소 운동용)", example = "50", nullable = true)
		Integer weight
	) {

	}
}
