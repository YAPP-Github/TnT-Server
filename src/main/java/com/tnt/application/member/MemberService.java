package com.tnt.application.member;

import static com.tnt.common.error.model.ErrorMessage.MEMBER_CONFLICT;
import static com.tnt.common.error.model.ErrorMessage.MEMBER_NOT_FOUND;
import static com.tnt.domain.member.MemberType.TRAINEE;
import static com.tnt.domain.member.MemberType.TRAINER;
import static com.tnt.dto.member.MemberProjection.MemberTypeDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.tnt.dto.member.request.UpdateMemberInfoRequest;
import com.tnt.dto.member.response.GetMemberInfoResponse;
import com.tnt.dto.member.response.GetMemberInfoResponse.GetTraineeInfo;
import com.tnt.dto.member.response.GetMemberInfoResponse.GetTrainerInfo;
import com.tnt.dto.member.response.UpdateMemberInfoResponse;
import com.tnt.dto.member.response.UpdateMemberInfoResponse.UpdateTraineeInfo;
import com.tnt.gateway.dto.response.CheckSessionResponse;
import com.tnt.infrastructure.mysql.repository.member.MemberRepository;
import com.tnt.infrastructure.mysql.repository.member.MemberSearchRepository;
import com.tnt.infrastructure.mysql.repository.pt.PtGoalRepository;

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
	private final PtGoalRepository ptGoalRepository;

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

			GetTrainerInfo getTrainerInfo = new GetTrainerInfo(activeTraineeCount, totalTraineeCount);

			memberInfo = new GetMemberInfoResponse(member.getName(), member.getEmail(), member.getProfileImageUrl(),
				member.getMemberType(), member.getSocialType(), getTrainerInfo, null);
		}

		if (member.getMemberType() == TRAINEE) {
			Trainee trainee = traineeService.getByMemberId(memberId);
			List<String> ptGoals = ptGoalService.getAllByTraineeId(trainee.getId())
				.stream()
				.map(PtGoal::getContent)
				.toList();
			boolean isConnected = ptService.isPtTrainerTraineeExistWithTraineeId(trainee.getId());

			GetTraineeInfo getTraineeInfo = new GetTraineeInfo(isConnected, member.getBirthday(),
				member.getAge(), trainee.getHeight(), trainee.getWeight(), trainee.getCautionNote(), ptGoals);

			memberInfo = new GetMemberInfoResponse(member.getName(), member.getEmail(), member.getProfileImageUrl(),
				member.getMemberType(), member.getSocialType(), null, getTraineeInfo);
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

	@Transactional
	public UpdateMemberInfoResponse updateMemberInfo(Long memberId, UpdateMemberInfoRequest request,
		String profileImageUrl) {
		Member findMember = getByMemberId(memberId);
		UpdateMemberInfoResponse memberInfo = null;

		findMember.updateName(request.name());
		findMember.updateProfileImageUrl(profileImageUrl);

		// 트레이너
		if (findMember.getMemberType() == TRAINER) {
			memberInfo = new UpdateMemberInfoResponse(findMember.getMemberType(), profileImageUrl, findMember.getName(),
				null);
		}

		// 트레이니
		if (findMember.getMemberType() == TRAINEE) {
			Trainee trainee = traineeService.getByMemberId(memberId);

			findMember.updateBirthday(request.birthday());
			trainee.updateTraineeInfo(request.height(), request.weight(), request.cautionNote());

			List<String> ptGoals = updatePtGoals(trainee, request.goalContents()).stream()
				.map(PtGoal::getContent)
				.toList();

			UpdateTraineeInfo updateTraineeInfo = new UpdateTraineeInfo(findMember.getBirthday(), findMember.getAge(),
				trainee.getHeight(), trainee.getWeight(), trainee.getCautionNote(), ptGoals);

			memberInfo = new UpdateMemberInfoResponse(findMember.getMemberType(), profileImageUrl, findMember.getName(),
				updateTraineeInfo);
		}

		return memberInfo;
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

	private List<PtGoal> updatePtGoals(Trainee trainee, List<String> newGoalContents) {
		// 기존 PT 목표들 조회
		List<PtGoal> currentPtGoals = ptGoalService.getAllByTraineeId(trainee.getId());

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
		Set<String> existingContents = currentPtGoals.stream().map(PtGoal::getContent).collect(Collectors.toSet());

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

		// 최종 목표 목록 리턴 (기존 유지된 목표 + 새로 추가된 목표)
		List<PtGoal> result = new ArrayList<>(currentPtGoals);
		result.addAll(newPtGoals);

		return result;
	}
}
