package com.tnt.member.application;

import static com.tnt.common.constant.ImageConstant.TRAINEE_DEFAULT_IMAGE;
import static com.tnt.common.constant.ImageConstant.TRAINER_DEFAULT_IMAGE;
import static com.tnt.member.domain.MemberType.TRAINEE;
import static com.tnt.member.domain.MemberType.TRAINER;
import static io.hypersistence.tsid.TSID.Factory.getTsid;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tnt.gateway.application.SessionService;
import com.tnt.member.application.repository.MemberRepository;
import com.tnt.member.domain.Member;
import com.tnt.member.domain.MemberType;
import com.tnt.member.dto.request.SignUpRequest;
import com.tnt.member.dto.response.SignUpResponse;
import com.tnt.pt.infrastructure.PtGoalRepository;
import com.tnt.trainee.domain.PtGoal;
import com.tnt.trainee.domain.Trainee;
import com.tnt.trainee.infrastructure.TraineeRepository;
import com.tnt.trainer.domain.Trainer;
import com.tnt.trainer.infrastructure.TrainerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SignUpService {

	private final SessionService sessionService;
	private final MemberService memberService;

	private final MemberRepository memberRepository;
	private final TrainerRepository trainerRepository;
	private final TraineeRepository traineeRepository;
	private final PtGoalRepository ptGoalRepository;

	@Transactional
	public Long signUp(SignUpRequest request) {
		memberService.validateMemberNotExists(request.socialId(), request.socialType());

		if (TRAINER.equals(request.memberType())) {
			return createTrainer(request);
		}

		return createTrainee(request);
	}

	@Transactional
	public SignUpResponse finishSignUpAfterImageUpload(String profileImageUrl, Long memberId, MemberType memberType) {
		Member member = memberService.getByMemberId(memberId);
		member.updateProfileImageUrl(profileImageUrl);

		String sessionId = String.valueOf(getTsid());

		sessionService.createSession(sessionId, String.valueOf(member.getId()));

		return new SignUpResponse(memberType, sessionId, member.getName(), member.getProfileImageUrl());
	}

	private Long createTrainer(SignUpRequest request) {
		Member member = createMember(request, TRAINER_DEFAULT_IMAGE, TRAINER);
		Trainer trainer = Trainer.builder()
			.member(member)
			.build();

		trainerRepository.save(trainer);

		return member.getId();
	}

	private Long createTrainee(SignUpRequest request) {
		Member member = createMember(request, TRAINEE_DEFAULT_IMAGE, TRAINEE);
		Trainee trainee = Trainee.builder()
			.member(member)
			.height(request.height())
			.weight(request.weight())
			.cautionNote(request.cautionNote())
			.build();

		trainee = traineeRepository.save(trainee);

		createPtGoals(trainee, request.goalContents());

		return member.getId();
	}

	private Member createMember(SignUpRequest request, String defaultImageUrl, MemberType memberType) {
		Member member = Member.builder()
			.socialId(request.socialId())
			.fcmToken(request.fcmToken())
			.email(request.socialEmail())
			.name(request.name())
			.profileImageUrl(defaultImageUrl)
			.birthday(request.birthday())
			.serviceAgreement(request.serviceAgreement())
			.collectionAgreement(request.collectionAgreement())
			.advertisementAgreement(request.advertisementAgreement())
			.socialType(request.socialType())
			.memberType(memberType)
			.build();

		return memberRepository.save(member);
	}

	private void createPtGoals(Trainee trainee, List<String> goalContents) {
		List<PtGoal> ptGoals = goalContents.stream()
			.map(content -> PtGoal.builder()
				.traineeId(trainee.getId())
				.content(content)
				.build())
			.toList();

		ptGoalRepository.saveAll(ptGoals);
	}
}
