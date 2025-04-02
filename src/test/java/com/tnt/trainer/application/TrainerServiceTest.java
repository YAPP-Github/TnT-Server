package com.tnt.trainer.application;

import static com.tnt.member.domain.MemberType.TRAINER;
import static com.tnt.trainer.domain.Trainer.INVITATION_CODE_LENGTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tnt.common.error.exception.NotFoundException;
import com.tnt.fixture.MemberFixture;
import com.tnt.fixture.TrainerFixture;
import com.tnt.member.domain.Member;
import com.tnt.member.domain.SocialType;
import com.tnt.trainer.application.repository.TrainerRepository;
import com.tnt.trainer.domain.Trainer;
import com.tnt.trainer.dto.response.InvitationCodeResponse;
import com.tnt.trainer.dto.response.InvitationCodeVerifyResponse;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

	@InjectMocks
	private TrainerService trainerService;

	@Mock
	private TrainerRepository trainerRepository;

	@Test
	@DisplayName("트레이너 초대 코드 불러오기 성공")
	void get_invitation_code_success() {
		// given
		Member member = MemberFixture.getTrainerMember1();

		Trainer trainer = TrainerFixture.getTrainer1(member);

		given(trainerRepository.findByMemberId(member.getId())).willReturn(trainer);

		// when
		InvitationCodeResponse response = trainerService.getInvitationCode(member.getId());

		// then
		assertThat(response.invitationCode()).isNotNull();
		assertThat(response.invitationCode()).hasSize(INVITATION_CODE_LENGTH);
	}

	@Test
	@DisplayName("트레이너 초대 코드 불러오기 실패 - 존재하지 않는 계정")
	void get_invitation_code_no_member_fail() {
		// given
		Long memberId = 99L;

		given(trainerRepository.findByMemberId(memberId)).willThrow(NotFoundException.class);

		// when & then
		assertThatThrownBy(() -> trainerService.getInvitationCode(memberId)).isInstanceOf(
			NotFoundException.class);
	}

	@Test
	@DisplayName("트레이너 초대 코드 재발급 성공")
	void reissue_invitation_code_success() {
		// given
		Member member = MemberFixture.getTrainerMemberWithId1();

		Trainer trainer = TrainerFixture.getTrainer1(member);

		String invitationCodeBefore = trainer.getInvitationCode();

		given(trainerRepository.findByMemberId(member.getId())).willReturn(trainer);

		// when
		InvitationCodeResponse response = trainerService.reissueInvitationCode(member.getId());

		// then
		assertThat(response.invitationCode()).isNotNull();
		assertThat(response.invitationCode()).hasSize(INVITATION_CODE_LENGTH);
		assertThat(response.invitationCode()).isNotEqualTo(invitationCodeBefore);
	}

	@Test
	@DisplayName("트레이너 초대 코드 인증 성공")
	void verify_invitation_code_success() {
		// given
		String code = "2H9DG4X3";

		Long memberId = 30L;
		String socialId = "1234567890";
		String email = "abc@gmail.com";
		String name = "김영명";
		String profileImageUrl = "https://profile.com/1234567890";

		Member member = Member.builder()
			.id(memberId)
			.socialId(socialId)
			.email(email)
			.name(name)
			.profileImageUrl(profileImageUrl)
			.serviceAgreement(true)
			.collectionAgreement(true)
			.advertisementAgreement(true)
			.socialType(SocialType.KAKAO)
			.memberType(TRAINER)
			.build();

		given(trainerRepository.existsByInvitationCode(code)).willReturn(true);
		given(trainerRepository.find(null, code)).willReturn(Trainer.builder().member(member).build());

		// when
		InvitationCodeVerifyResponse response = trainerService.verifyInvitationCode(code);

		// then
		assertThat(response.isVerified()).isTrue();
	}
}
