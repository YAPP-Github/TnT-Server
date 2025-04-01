package com.tnt.member.application;

import static com.tnt.member.domain.SocialType.KAKAO;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
import com.tnt.fixture.TrainerFixture;
import com.tnt.gateway.dto.response.CheckSessionResponse;
import com.tnt.member.application.repository.MemberRepository;
import com.tnt.member.domain.Member;
import com.tnt.member.domain.SocialType;
import com.tnt.member.dto.MemberProjection;
import com.tnt.pt.application.PtService;
import com.tnt.trainer.application.TrainerService;
import com.tnt.trainer.domain.Trainer;

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
	@DisplayName("memberId로 회원 조회 성공")
	void get_member_with_member_id_success() {
		// given
		Member trainerMember = MemberFixture.getTrainerMemberWithId1();

		given(memberRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(trainerMember);

		// when
		Member result = memberService.getByMemberId(requireNonNull(trainerMember).getId());

		// then
		assertThat(result).isNotNull().isEqualTo(trainerMember);
		verify(memberRepository).findByIdAndDeletedAtIsNull(1L);
	}

	@Test
	@DisplayName("존재하지 않는 memberId로 회원 조회시 실패")
	void get_member_with_member_id_error() {
		// given
		Long memberId = 999L;

		given(memberRepository.findByIdAndDeletedAtIsNull(999L)).willThrow(NotFoundException.class);

		// when & then
		assertThrows(NotFoundException.class, () -> memberService.getByMemberId(memberId));
		verify(memberRepository).findByIdAndDeletedAtIsNull(999L);
	}

	@Test
	@DisplayName("socialId와 socialType으로 회원 조회 성공")
	void get_member_with_social_id_and_type_success() {
		// given
		Member member = MemberFixture.getTrainerMemberWithId1();
		String socialId = member.getSocialId();
		SocialType socialType = member.getSocialType();

		given(memberRepository.findBySocialIdAndSocialTypeAndDeletedAtIsNull(socialId, socialType)).willReturn(member);

		// when
		Member result = memberService.getBySocialIdAndSocialType(socialId, socialType);

		// then
		assertThat(result).isNotNull().isEqualTo(member);
		verify(memberRepository).findBySocialIdAndSocialTypeAndDeletedAtIsNull(socialId, socialType);
	}

	@Test
	@DisplayName("회원 중복 검증 성공")
	void validate_member_not_exists_success() {
		// given
		String socialId = "user";
		SocialType socialType = KAKAO;

		given(memberRepository.existsBySocialIdAndSocialTypeAndDeletedAtIsNull(socialId, socialType)).willReturn(false);

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

		given(memberRepository.existsBySocialIdAndSocialTypeAndDeletedAtIsNull(socialId, socialType)).willReturn(
			true);

		// when & then
		assertThrows(ConflictException.class, () -> memberService.validateMemberNotExists(socialId, socialType));
	}

	@Test
	@DisplayName("memberId로 회원 타입 조회 성공")
	void get_member_type_success() {
		// given
		Member member = MemberFixture.getTrainerMemberWithId1();
		Long memberId = member.getId();

		Trainer trainer = TrainerFixture.getTrainer1(member);

		given(memberRepository.findMemberType(memberId)).willReturn(
			new MemberProjection.MemberTypeDto(member.getMemberType()));
		given(trainerService.getByMemberId(memberId)).willReturn(trainer);
		given(ptService.isPtTrainerTraineeExistWithTrainerId(trainer.getId())).willReturn(true);

		// when
		CheckSessionResponse checkSessionResponse = memberService.getMemberType(memberId);

		// then
		assertThat(checkSessionResponse.memberType()).isEqualTo(member.getMemberType());
	}
}
