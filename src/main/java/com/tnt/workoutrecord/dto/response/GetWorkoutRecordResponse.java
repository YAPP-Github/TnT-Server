package com.tnt.workoutrecord.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.tnt.workoutrecord.domain.RecordType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "운동 기록 조회 응답")
public record GetWorkoutRecordResponse(
	@Schema(description = "운동 기록 ID", example = "1")
	Long workoutRecordId,

	@Schema(description = "회원 ID", example = "1")
	Long memberId,

	@Schema(description = "PT 수업 ID (nullable)", example = "5", nullable = true)
	Long ptLessonId,

	@Schema(description = "운동 기록 날짜", example = "2025-03-20T14:30:00")
	LocalDateTime date,

	@Schema(description = "기록 타입 (PT 또는 TRAINEE)", example = "PT")
	RecordType recordType,

	@Schema(description = "피드백", example = "오늘 운동 잘했습니다!", nullable = true)
	String feedback,

	@Schema(description = "운동 사진 URL 리스트 (최대 6장)")
	List<String> imageUrls,

	@Schema(description = "루틴 목록")
	List<RoutineResponse> routines
) {
	@Schema(description = "루틴 정보")
	public record RoutineResponse(
		@Schema(description = "루틴 ID", example = "1")
		Long routineId,

		@Schema(description = "운동 ID", example = "10")
		Long workoutId,

		@Schema(description = "세트 목록")
		List<SetResponse> sets
	) {
	}

	@Schema(description = "세트 정보")
	public record SetResponse(
		@Schema(description = "세트 ID", example = "1")
		Long setId,

		@Schema(description = "지속 시간 (분, 유산소 운동용)", example = "30", nullable = true)
		Integer durationMinutes,

		@Schema(description = "반복 횟수 (무산소 운동용)", example = "12", nullable = true)
		Integer repetition,

		@Schema(description = "무게 (kg)", example = "50", nullable = true)
		Integer weight
	) {
	}
}
