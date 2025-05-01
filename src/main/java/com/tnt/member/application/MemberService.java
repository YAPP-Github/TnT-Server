package com.tnt.member.application;

import static com.tnt.common.error.model.ErrorMessage.MEMBER_CONFLICT;
import static com.tnt.member.domain.MemberType.TRAINEE;
import static com.tnt.member.domain.MemberType.TRAINER;
import static com.tnt.member.dto.MemberProjection.MemberTypeDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tnt.common.error.exception.ConflictException;
import com.tnt.gateway.dto.response.CheckSessionResponse;
import com.tnt.member.application.repository.MemberRepository;
import com.tnt.member.domain.Member;
import com.tnt.member.domain.SocialType;
import com.tnt.member.dto.MemberInfo;
import com.tnt.member.dto.request.UpdateMemberInfoRequest;
import com.tnt.pt.application.PtService;
import com.tnt.pt.domain.PtTrainerTrainee;
import com.tnt.trainee.application.PtGoalService;
import com.tnt.trainee.application.TraineeService;
import com.tnt.trainee.application.repository.PtGoalRepository;
import com.tnt.trainee.application.repository.TraineeRepository;
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
	private final TraineeRepository traineeRepository;
	private final PtGoalRepository ptGoalRepository;

	@Transactional(readOnly = true)
	public MemberInfo getMemberInfo(Long memberId) {
		Member member = getByMemberId(memberId);
		MemberInfo memberInfo = null;

		if (member.getMemberType() == TRAINER) {
			Trainer trainer = trainerService.getByMemberId(memberId);
			List<PtTrainerTrainee> ptTrainerTrainees = ptService.getAllPtTrainerTraineeWithTrainerIdWithDeleted(
				trainer.getId());

			int activeTraineeCount = (int)ptTrainerTrainees.stream()
				.filter(ptTrainerTrainee -> ptTrainerTrainee.getDeletedAt() == null)
				.count();

			int totalTraineeCount = ptTrainerTrainees.size();

			MemberInfo.TrainerInfo trainerInfo = new MemberInfo.TrainerInfo(activeTraineeCount, totalTraineeCount);

			memberInfo = new MemberInfo(member.getName(), member.getEmail(), member.getProfileImageUrl(),
				member.getMemberType(), member.getSocialType(), trainerInfo, null);
		}

		if (member.getMemberType() == TRAINEE) {
			Trainee trainee = traineeService.getByMemberId(memberId);
			List<String> ptGoals = ptGoalService.getAllByTraineeId(trainee.getId())
				.stream()
				.map(PtGoal::getContent)
				.toList();
			boolean isConnected = ptService.isPtTrainerTraineeExistWithTraineeId(trainee.getId());

			MemberInfo.TraineeInfo traineeInfo = new MemberInfo.TraineeInfo(isConnected, member.getBirthday(),
				member.getAge(),
				trainee.getHeight(), trainee.getWeight(), trainee.getCautionNote(), ptGoals);

			memberInfo = new MemberInfo(member.getName(), member.getEmail(), member.getProfileImageUrl(),
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

	@Transactional
	public void updateMemberProfileImage(Long memberId, String profileImageUrl) {
		Member member = getByMemberId(memberId);

		member.updateProfileImageUrl(profileImageUrl);

		memberRepository.save(member);
	}

	@Transactional
	public void updateMemberInfo(Long memberId, UpdateMemberInfoRequest request) {
		Member member = getByMemberId(memberId);

		member.updateName(request.name());

		if (member.getMemberType() == TRAINEE) {
			Trainee trainee = traineeService.getByMemberId(memberId);

			member.updateBirthday(request.birthday());
			trainee.updateTraineeInfo(request.height(), request.weight(), request.cautionNote());
			updatePtGoals(trainee, request.goalContents());

			traineeRepository.save(trainee);
		}

		memberRepository.save(member);
	}

	public void validateMemberNotExists(String socialId, SocialType socialType) {
		if (memberRepository.existsBySocialIdAndSocialType(socialId, socialType)) {
			throw new ConflictException(MEMBER_CONFLICT);
		}
	}

	public Member getByMemberId(Long memberId) {
		return memberRepository.findById(memberId);
	}

	private void updatePtGoals(Trainee trainee, List<String> newGoalContents) {
		// 기존 PT 목표들 조회
		List<PtGoal> currentPtGoals = new ArrayList<>(ptGoalService.getAllByTraineeId(trainee.getId()));

		// 기존 목표 중 더 이상 필요없는 목표 삭제
		if (!currentPtGoals.isEmpty()) {
			List<PtGoal> goalsToDelete = new ArrayList<>();

			for (PtGoal currentGoal : currentPtGoals) {
				if (!newGoalContents.contains(currentGoal.getContent())) {
					goalsToDelete.add(currentGoal);
				}
			}

			if (!goalsToDelete.isEmpty()) {
				ptGoalRepository.deleteAll(goalsToDelete);
				currentPtGoals.removeAll(goalsToDelete);
			}
		}

		// 새로운 목표 추가 (기존에 없는 것만)
		Set<String> existingContents = currentPtGoals.stream()
			.map(PtGoal::getContent)
			.collect(Collectors.toSet());

		List<PtGoal> newPtGoals = newGoalContents.stream()
			.filter(content -> !existingContents.contains(content))
			.map(content -> PtGoal.builder()
				.traineeId(trainee.getId())
				.content(content)
				.build())
			.toList();

		if (!newPtGoals.isEmpty()) {
			ptGoalRepository.saveAll(newPtGoals);
		}
	}
}
