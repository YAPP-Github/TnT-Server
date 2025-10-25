package com.tnt.member.application;

import static com.tnt.common.constant.ImageConstant.TRAINEE_DEFAULT_IMAGE;
import static com.tnt.common.constant.ImageConstant.TRAINER_DEFAULT_IMAGE;
import static com.tnt.common.error.model.ErrorMessage.MEMBER_CONFLICT;
import static com.tnt.member.domain.MemberType.TRAINEE;
import static com.tnt.member.domain.MemberType.TRAINER;
import static com.tnt.member.dto.MemberProjection.MemberTypeDto;
import static java.util.Objects.isNull;

import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tnt.common.error.exception.ConflictException;
import com.tnt.gateway.dto.response.CheckSessionResponse;
import com.tnt.member.application.repository.MemberRepository;
import com.tnt.member.domain.Member;
import com.tnt.member.domain.SocialType;
import com.tnt.member.dto.ProfileUpdate;
import com.tnt.member.dto.request.UpdateMemberInfoRequest;
import com.tnt.member.dto.response.MemberInfoResponse;
import com.tnt.pt.application.PtService;
import com.tnt.pt.domain.PtTrainerTrainee;
import com.tnt.trainee.application.TraineeService;
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
	private final PtService ptService;

	private final MemberRepository memberRepository;
	private final TraineeRepository traineeRepository;

	@Transactional(readOnly = true)
	public MemberInfoResponse getMemberInfo(Long memberId) {
		Member member = getByMemberId(memberId);
		MemberInfoResponse memberInfoResponse = null;

		if (member.getMemberType() == TRAINER) {
			Trainer trainer = trainerService.getByMemberId(memberId);
			List<PtTrainerTrainee> ptTrainerTrainees = ptService.getAllPtTrainerTraineeWithTrainerIdWithDeleted(
				trainer.getId());

			int activeTraineeCount = (int)ptTrainerTrainees.stream()
				.filter(ptTrainerTrainee -> ptTrainerTrainee.getDeletedAt() == null)
				.count();

			int totalTraineeCount = ptTrainerTrainees.size();

			MemberInfoResponse.TrainerInfo trainerInfo = new MemberInfoResponse.TrainerInfo(activeTraineeCount,
				totalTraineeCount);

			memberInfoResponse = new MemberInfoResponse(member.getName(), member.getEmail(),
				member.getProfileImageUrl(),
				member.getMemberType(), member.getSocialType(), trainerInfo, null);
		}

		if (member.getMemberType() == TRAINEE) {
			Trainee trainee = traineeService.getByMemberId(memberId);
			List<PtGoal> ptGoals = trainee.getPtGoals();
			boolean isConnected = ptService.isPtTrainerTraineeExistWithTraineeId(trainee.getId());

			MemberInfoResponse.TraineeInfo traineeInfo = new MemberInfoResponse.TraineeInfo(isConnected,
				member.getBirthday(),
				member.getAge(),
				trainee.getHeight(), trainee.getWeight(), trainee.getCautionNote(), ptGoals);

			memberInfoResponse = new MemberInfoResponse(member.getName(), member.getEmail(),
				member.getProfileImageUrl(),
				member.getMemberType(), member.getSocialType(), null, traineeInfo);
		}

		return memberInfoResponse;
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
	public ProfileUpdate checkMemberProfileImage(Long memberId, boolean removeImage,
		@Nullable MultipartFile profileImage) {
		Member member = getByMemberId(memberId);
		String currentImageUrl = member.getProfileImageUrl();
		String changeImageUrl = "";
		boolean removeCurrentImage = true;
		boolean isCurrentImageDefault =
			currentImageUrl.equals(TRAINER_DEFAULT_IMAGE) || currentImageUrl.equals(TRAINEE_DEFAULT_IMAGE);

		// 새 이미지 없음
		if (isNull(profileImage)) {
			// 이미지 삭제 요청 - 현재 이미지가 기본 이미지가 아닌 경우
			if (removeImage && !isCurrentImageDefault) {
				changeImageUrl =
					member.getMemberType() == TRAINER ? TRAINER_DEFAULT_IMAGE : TRAINEE_DEFAULT_IMAGE;
			} else if (!removeImage && isCurrentImageDefault) { // 이미지 유지 요청 - 현재 이미지가 기본 이미지인 경우
				changeImageUrl = currentImageUrl;
				removeCurrentImage = false;
			} else { // 이미지 유지 요청 - 현재 이미지가 기본 이미지가 아닌 경우
				removeCurrentImage = false;
			}
		} else { // 새 이미지 있음
			// 이미지 수정 요청 - 현재 이미지가 기본 이미지인 경우
			if (isCurrentImageDefault) {
				removeCurrentImage = false;
			}
		}

		return new ProfileUpdate(currentImageUrl, changeImageUrl, removeCurrentImage,
			isCurrentImageDefault);
	}

	@Transactional
	public void updateMemberInfo(Long memberId, UpdateMemberInfoRequest request, String profileImageUrl) {
		Member member = getByMemberId(memberId);

		if (member.getMemberType() == TRAINEE) {
			Trainee trainee = traineeService.getByMemberId(memberId);

			member.updateBirthday(request.birthday());
			trainee.updateTraineeInfo(request.height(), request.weight(), request.cautionNote(), request.ptGoals());

			traineeRepository.save(trainee);
		}

		member.updateName(request.name());
		member.updateProfileImageUrl(profileImageUrl);

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
}
