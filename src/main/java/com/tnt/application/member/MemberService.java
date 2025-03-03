package com.tnt.application.member;

import static com.tnt.common.error.model.ErrorMessage.MEMBER_CONFLICT;
import static com.tnt.common.error.model.ErrorMessage.MEMBER_NOT_FOUND;
import static com.tnt.domain.member.MemberType.TRAINEE;
import static com.tnt.domain.member.MemberType.TRAINER;
import static com.tnt.dto.member.MemberProjection.MemberTypeDto;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tnt.application.pt.PtService;
import com.tnt.application.trainee.PtGoalService;
import com.tnt.application.trainee.TraineeService;
import com.tnt.application.trainer.TrainerService;
import com.tnt.common.error.exception.ConflictException;
import com.tnt.common.error.exception.NotFoundException;
import com.tnt.domain.member.Member;
import com.tnt.domain.member.SocialType;
import com.tnt.domain.pt.PtTrainerTrainee;
import com.tnt.domain.trainee.PtGoal;
import com.tnt.domain.trainee.Trainee;
import com.tnt.domain.trainer.Trainer;
import com.tnt.dto.member.response.GetMemberInfoResponse;
import com.tnt.dto.member.response.GetMemberInfoResponse.TraineeInfo;
import com.tnt.dto.member.response.GetMemberInfoResponse.TrainerInfo;
import com.tnt.gateway.dto.response.CheckSessionResponse;
import com.tnt.infrastructure.mysql.repository.member.MemberRepository;
import com.tnt.infrastructure.mysql.repository.member.MemberSearchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final TrainerService trainerService;
	private final TraineeService traineeService;
	private final PtGoalService ptGoalService;
	private final PtService ptService;

	private final MemberRepository memberRepository;
	private final MemberSearchRepository memberSearchRepository;

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
		MemberTypeDto memberTypeDto = memberSearchRepository.findMemberType(memberId)
			.orElseThrow(() -> new NotFoundException(MEMBER_NOT_FOUND));

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
		memberRepository.findBySocialIdAndSocialTypeAndDeletedAtIsNull(socialId, socialType)
			.ifPresent(member -> {
				throw new ConflictException(MEMBER_CONFLICT);
			});
	}

	public Member getByMemberId(Long memberId) {
		return memberRepository.findByIdAndDeletedAtIsNull(memberId)
			.orElseThrow(() -> new NotFoundException(MEMBER_NOT_FOUND));
	}

	public Member getBySocialIdAndSocialType(String socialId, SocialType socialType) {
		return memberRepository.findBySocialIdAndSocialTypeAndDeletedAtIsNull(socialId, socialType)
			.orElseThrow(() -> new NotFoundException(MEMBER_NOT_FOUND));
	}
}
