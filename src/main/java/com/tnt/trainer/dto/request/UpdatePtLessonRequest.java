package com.tnt.trainer.dto.request;

import java.time.LocalDateTime;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "PT 수업 수정 요청")
public record UpdatePtLessonRequest(
	@Schema(description = "수업 시작 날짜 및 시간", example = "2025-03-20T10:00:00", nullable = false)
	@NotNull(message = "수업 시작 시간은 필수입니다.")
	LocalDateTime lessonStart,

	@Schema(description = "수업 끝 날짜 및 시간", example = "2025-03-20T11:00:00", nullable = false)
	@NotNull(message = "수업 종료 시간은 필수입니다.")
	LocalDateTime lessonEnd,

	@Schema(description = "메모", example = "하체 운동 시키기", nullable = true)
	@Length(max = 30, message = "메모는 30자 이하여야 합니다.")
	String memo
) {

}
