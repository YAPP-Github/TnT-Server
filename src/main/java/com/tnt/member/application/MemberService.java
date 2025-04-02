package com.tnt.member.application;

import static com.tnt.common.error.model.ErrorMessage.MEMBER_CONFLICT;
import static com.tnt.member.domain.MemberType.TRAINEE;
import static com.tnt.member.domain.MemberType.TRAINER;
import static com.tnt.member.dto.MemberProjection.MemberTypeDto;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tnt.common.error.exception.ConflictException;
import com.tnt.gateway.dto.response.CheckSessionResponse;
import com.tnt.member.application.repository.MemberRepository;
import com.tnt.member.domain.Member;
import com.tnt.member.domain.SocialType;
import com.tnt.member.dto.response.GetMemberInfoResponse;
import com.tnt.member.dto.response.GetMemberInfoResponse.TraineeInfo;
import com.tnt.member.dto.response.GetMemberInfoResponse.TrainerInfo;
import com.tnt.pt.application.PtService;
import com.tnt.pt.domain.PtTrainerTrainee;
import com.tnt.trainee.application.PtGoalService;
import com.tnt.trainee.application.TraineeService;
import com.tnt.trainee.domain.PtGoal;
import com.tnt.trainee.domain.Trainee;
import com.tnt.trainer.application.TrainerService;
import com.tnt.trainer.domain.Trainer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final TrainerService trainerService;
	private final TraineeService traineeService;
	private final PtGoalService ptGoalService;
	private final PtService ptService;

	private final MemberRepository memberRepository;

	@Transactional(readOnly = true)
	public GetMemberInfoResponse getMemberInfo(Long memberId) {
		Member member = getByMemberId(memberId);
		GetMemberInfoResponse memberInfo = null;

		if (member.getMemberType() == TRAINER) {
			Trainer trainer = trainerService.getByMemberId(memberId);
			List<PtTrainerTrainee> ptTrainerTrainees = ptService.getAllPtTrainerTraineeWithTrainerIdWithDeleted(
				trainer.getId());

			int activeTraineeCount = (int)ptTrainerTrainees.stream()
				.filter(ptTrainerTrainee -> ptTrainerTrainee.getDeletedAt() == null)
				.count();

			int totalTraineeCount = ptTrainerTrainees.size();

			TrainerInfo trainerInfo = new TrainerInfo(activeTraineeCount, totalTraineeCount);

			memberInfo = new GetMemberInfoResponse(member.getName(), member.getEmail(), member.getProfileImageUrl(),
				member.getMemberType(), member.getSocialType(), trainerInfo, null);
		} else if (member.getMemberType() == TRAINEE) {
			Trainee trainee = traineeService.getByMemberId(memberId);
			List<String> ptGoals = ptGoalService.getAllByTraineeId(trainee.getId()).stream().map(
				PtGoal::getContent).toList();
			boolean isConnected = ptService.isPtTrainerTraineeExistWithTraineeId(trainee.getId());

			TraineeInfo traineeInfo = new TraineeInfo(isConnected, member.getBirthday(),
				member.getAge(), trainee.getHeight(), trainee.getWeight(), trainee.getCautionNote(), ptGoals);

			memberInfo = new GetMemberInfoResponse(member.getName(), member.getEmail(), member.getProfileImageUrl(),
				member.getMemberType(), member.getSocialType(), null, traineeInfo);
		}

		return memberInfo;
	}

	@Transactional(readOnly = true)
	public CheckSessionResponse getMemberType(Long memberId) {
		MemberTypeDto memberTypeDto = memberRepository.findMemberType(memberId);
		boolean isConnected = false;

		if (memberTypeDto.memberType() == TRAINER) {
			Trainer trainer = trainerService.getByMemberId(memberId);
			isConnected = ptService.isPtTrainerTraineeExistWithTrainerId(trainer.getId());
		} else if (memberTypeDto.memberType() == TRAINEE) {
			Trainee trainee = traineeService.getByMemberId(memberId);
			isConnected = ptService.isPtTrainerTraineeExistWithTraineeId(trainee.getId());
		}

		return new CheckSessionResponse(memberTypeDto.memberType(), isConnected);
	}

	public void validateMemberNotExists(String socialId, SocialType socialType) {
		if (memberRepository.existsBySocialIdAndSocialType(socialId, socialType)) {
			throw new ConflictException(MEMBER_CONFLICT);
		}
	}

	public Member getByMemberId(Long memberId) {
		return memberRepository.findById(memberId);
	}

	public Member getBySocialIdAndSocialType(String socialId, SocialType socialType) {
		return memberRepository.findBySocialIdAndSocialType(socialId, socialType);
	}
}
