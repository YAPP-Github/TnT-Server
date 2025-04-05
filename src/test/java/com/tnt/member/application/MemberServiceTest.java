package com.tnt.member.application;

import static com.tnt.member.domain.SocialType.KAKAO;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tnt.common.error.exception.ConflictException;
import com.tnt.common.error.exception.NotFoundException;
import com.tnt.fixture.MemberFixture;
import com.tnt.member.application.repository.MemberRepository;
import com.tnt.member.domain.Member;
import com.tnt.member.domain.SocialType;
import com.tnt.pt.application.PtService;
import com.tnt.trainer.application.TrainerService;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@InjectMocks
	private MemberService memberService;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PtService ptService;

	@Mock
	private TrainerService trainerService;

	@Test
	@DisplayName("존재하지 않는 memberId로 회원 조회시 실패")
	void get_member_with_member_id_error() {
		// given
		Long memberId = 999L;

		given(memberRepository.findById(999L)).willThrow(NotFoundException.class);

		// when & then
		assertThrows(NotFoundException.class, () -> memberService.getByMemberId(memberId));
		verify(memberRepository).findById(999L);
	}

	@Test
	@DisplayName("회원 중복 검증 성공")
	void validate_member_not_exists_success() {
		// given
		String socialId = "user";
		SocialType socialType = KAKAO;

		given(memberRepository.existsBySocialIdAndSocialType(socialId, socialType)).willReturn(false);

		// when & then
		assertDoesNotThrow(() -> memberService.validateMemberNotExists(socialId, socialType));
	}

	@Test
	@DisplayName("이미 존재하는 회원 검증시 실패")
	void validate_member_exists_error() {
		// given
		Member existingMember = MemberFixture.getTrainerMemberWithId1();
		String socialId = existingMember.getSocialId();
		SocialType socialType = existingMember.getSocialType();

		given(memberRepository.existsBySocialIdAndSocialType(socialId, socialType)).willReturn(
			true);

		// when & then
		assertThrows(ConflictException.class, () -> memberService.validateMemberNotExists(socialId, socialType));
	}

	// TODO: UPDATE
	// @Test
	// @DisplayName("memberId로 회원 타입 조회 성공")
	// void get_member_type_success() {
	// 	// given
	// 	Member member = MemberFixture.getTrainerMemberWithId1();
	// 	Long memberId = member.getId();
	//
	// 	Trainer trainer = TrainerFixture.getTrainer1(member);
	//
	// 	given(memberRepository.findMemberType(memberId)).willReturn(
	// 		new MemberProjection.MemberTypeDto(member.getMemberType()));
	// 	given(trainerService.getByMemberId(memberId)).willReturn(trainer);
	// 	given(ptService.isPtTrainerTraineeExistWithTrainerId(trainer.getId())).willReturn(true);
	//
	// 	// when
	// 	CheckSessionResponse checkSessionResponse = memberService.getMemberType(memberId);
	//
	// 	// then
	// 	assertThat(checkSessionResponse.memberType()).isEqualTo(member.getMemberType());
	// }
}
