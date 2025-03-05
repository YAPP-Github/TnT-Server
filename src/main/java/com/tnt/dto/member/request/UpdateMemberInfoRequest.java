package com.tnt.dto.member.request;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

@Schema(description = "회원 정보 수정 API 요청")
public record UpdateMemberInfoRequest(
	@Schema(description = "회원 이름", example = "홍길동", nullable = false)
	@NotBlank(message = "회원 이름은 필수입니다.")
	String name,

	@Schema(description = "생년월일", example = "2025-01-01", nullable = true)
	@Past(message = "생년월일은 과거 날짜여야 합니다.")
	LocalDate birthday,

	@Schema(description = "키 (cm)", example = "180.5", nullable = true)
	@Digits(integer = 3, fraction = 2, message = "키는 정수부 3자리, 소수점 2자리까지 입력 가능합니다.")
	Double height,

	@Schema(description = "몸무게 (kg)", example = "75.5", nullable = true)
	@Digits(integer = 3, fraction = 2, message = "몸무게는 정수부 3자리, 소수점 2자리까지 입력 가능합니다.")
	Double weight,

	@Schema(description = "주의사항", example = "가냘퍼요", nullable = true)
	String cautionNote,

	@Schema(description = "PT 목적들", example = "[\"체중 감량\", \"근력 향상\"]", nullable = false)
	List<String> goalContents
) {

}
