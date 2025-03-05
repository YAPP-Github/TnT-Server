package com.tnt.dto.member.response;

import java.time.LocalDate;
import java.util.List;

import com.tnt.domain.member.MemberType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 정보 수정 API 응답")
public record UpdateMemberInfoResponse(
	@Schema(description = "회원 타입", example = "TRAINER", nullable = false)
	MemberType memberType,

	@Schema(description = "프로필 사진 URL", example = "https://images.tntapp.co.kr/profiles/trainers/basic_profile_trainer.svg", nullable = false)
	String profileImageUrl,

	@Schema(description = "회원 이름", example = "홍길동", nullable = false)
	String name,

	@Schema(description = "트레이니 정보", nullable = true)
	UpdateTraineeInfo trainee
) {

	public record UpdateTraineeInfo(
		@Schema(description = "생년월일", example = "2025-01-01", nullable = true)
		LocalDate birthday,

		@Schema(description = "나이", example = "25", nullable = true)
		Integer age,

		@Schema(description = "키 (cm)", example = "180.5", nullable = true)
		Double height,

		@Schema(description = "몸무게 (kg)", example = "75.5", nullable = true)
		Double weight,

		@Schema(description = "주의사항", example = "가냘퍼요", nullable = true)
		String cautionNote,

		@Schema(description = "PT 목적들", example = "[\"체중 감량\", \"근력 향상\"]", nullable = false)
		List<String> ptGoals
	) {

	}
}
