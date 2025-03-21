package com.tnt.presentation.member;

import static com.tnt.common.constant.ImageConstant.TRAINEE_DEFAULT_IMAGE;
import static com.tnt.common.constant.ImageConstant.TRAINER_DEFAULT_IMAGE;
import static com.tnt.domain.member.MemberType.TRAINEE;
import static com.tnt.domain.member.MemberType.TRAINER;
import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tnt.application.member.MemberService;
import com.tnt.application.member.SignUpService;
import com.tnt.application.member.WithdrawService;
import com.tnt.application.s3.S3Service;
import com.tnt.dto.member.MemberInfo;
import com.tnt.dto.member.WithdrawDto;
import com.tnt.dto.member.request.SignUpRequest;
import com.tnt.dto.member.request.UpdateMemberInfoRequest;
import com.tnt.dto.member.response.SignUpResponse;
import com.tnt.gateway.config.AuthMember;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "회원", description = "회원 관련 API")
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

	private final S3Service s3Service;
	private final SignUpService signUpService;
	private final WithdrawService withdrawService;
	private final MemberService memberService;

	@Operation(summary = "회원가입 API")
	@PostMapping(value = "/sign-up", consumes = MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(CREATED)
	public SignUpResponse signUp(@RequestPart("request") @Valid SignUpRequest request,
		@RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
		Long memberId = signUpService.signUp(request);
		String profileImageUrl = s3Service.uploadProfileImage(profileImage, request.memberType());

		return signUpService.finishSignUpAfterImageUpload(profileImageUrl, memberId, request.memberType());
	}

	@Operation(summary = "회원 조회 API")
	@GetMapping
	@ResponseStatus(OK)
	public MemberInfo getMemberInfo(@AuthMember Long memberId) {
		return memberService.getMemberInfo(memberId);
	}

	@Operation(summary = "프로필 사진 수정 API")
	@PostMapping(value = "/update-profile", consumes = MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(OK)
	public void updateMemberProfileImage(@AuthMember Long memberId,
		@RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
		MemberInfo memberInfo = memberService.getMemberInfo(memberId);
		String currentProfileImageUrl = memberInfo.profileImageUrl();

		s3Service.deleteProfileImage(currentProfileImageUrl);

		if (isNull(profileImage)) {
			if (memberInfo.memberType() == TRAINER) {
				currentProfileImageUrl = TRAINER_DEFAULT_IMAGE;
			}

			if (memberInfo.memberType() == TRAINEE) {
				currentProfileImageUrl = TRAINEE_DEFAULT_IMAGE;
			}
		} else {
			currentProfileImageUrl = s3Service.uploadProfileImage(profileImage, memberInfo.memberType());
		}

		memberService.updateMemberProfileImage(memberId, currentProfileImageUrl);
	}

	@Operation(summary = "회원 정보 수정 API")
	@PostMapping
	@ResponseStatus(OK)
	public void updateMemberInfo(@AuthMember Long memberId, @RequestBody @Valid UpdateMemberInfoRequest request) {
		memberService.updateMemberInfo(memberId, request);
	}

	@Operation(summary = "회원 탈퇴 API")
	@PostMapping("/withdraw")
	@ResponseStatus(OK)
	public void withdraw(@AuthMember Long memberId) {
		WithdrawDto withdrawDto = withdrawService.withdraw(memberId);

		s3Service.deleteProfileImage(withdrawDto.profileImageUrl());
	}
}
