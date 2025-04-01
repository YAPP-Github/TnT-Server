package com.tnt.member.presentation;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tnt.gateway.config.AuthMember;
import com.tnt.image.application.S3Service;
import com.tnt.member.application.MemberService;
import com.tnt.member.application.SignUpService;
import com.tnt.member.application.WithdrawService;
import com.tnt.member.dto.WithdrawDto;
import com.tnt.member.dto.request.SignUpRequest;
import com.tnt.member.dto.response.GetMemberInfoResponse;
import com.tnt.member.dto.response.SignUpResponse;

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
	public GetMemberInfoResponse getMemberInfo(@AuthMember Long memberId) {
		return memberService.getMemberInfo(memberId);
	}

	@Operation(summary = "회원 탈퇴 API")
	@PostMapping("/withdraw")
	@ResponseStatus(OK)
	public void withdraw(@AuthMember Long memberId) {
		WithdrawDto withdrawDto = withdrawService.withdraw(memberId);

		s3Service.deleteProfileImage(withdrawDto.profileImageUrl());
	}
}
