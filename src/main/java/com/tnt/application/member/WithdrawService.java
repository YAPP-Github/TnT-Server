package com.tnt.application.member;

import static com.tnt.domain.member.MemberType.TRAINEE;
import static com.tnt.domain.member.MemberType.TRAINER;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tnt.application.pt.PtService;
import com.tnt.application.trainee.DietService;
import com.tnt.application.trainee.PtGoalService;
import com.tnt.application.trainee.TraineeService;
import com.tnt.application.trainer.TrainerService;
import com.tnt.common.error.exception.NotFoundException;
import com.tnt.domain.member.Member;
import com.tnt.domain.pt.PtLesson;
import com.tnt.domain.pt.PtTrainerTrainee;
import com.tnt.domain.trainee.Diet;
import com.tnt.domain.trainee.PtGoal;
import com.tnt.domain.trainee.Trainee;
import com.tnt.domain.trainer.Trainer;
import com.tnt.dto.member.WithdrawDto;
import com.tnt.gateway.service.SessionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WithdrawService {

	private final SessionService sessionService;
	private final MemberService memberService;
	private final TrainerService trainerService;
	private final TraineeService traineeService;
	private final PtGoalService ptGoalService;
	private final DietService dietService;
	private final PtService ptService;

	@Transactional
	public WithdrawDto withdraw(Long memberId) {
		Member member = memberService.getMemberWithMemberId(memberId);

		deleteMemberData(member);

		sessionService.removeSession(String.valueOf(memberId));

		return new WithdrawDto(member.getSocialId(), member.getSocialType(), member.getProfileImageUrl());
	}

	private void deleteMemberData(Member member) {
		if (member.getMemberType() == TRAINER) {
			Trainer trainer = trainerService.getTrainerWithMemberId(member.getId());

			if (ptService.isPtTrainerTraineeExistWithTrainerId(trainer.getId())) {
				List<PtTrainerTrainee> ptTrainerTrainee = ptService.getAllPtTrainerTraineeWithTrainerId(
					trainer.getId());

				List<PtLesson> ptLessons = ptTrainerTrainee.stream().map(ptService::getPtLessonWithPtTrainerTrainee)
					.flatMap(List::stream)
					.toList();

				ptLessons.forEach(PtLesson::softDelete);
				ptTrainerTrainee.forEach(PtTrainerTrainee::softDelete);
			}

			trainer.softDelete();
		}

		if (member.getMemberType() == TRAINEE) {
			Trainee trainee = traineeService.getTraineeWithMemberId(member.getId());
			List<PtGoal> ptGoals = ptGoalService.getAllPtGoalsWithTraineeId(trainee.getId());
			List<Diet> diets = dietService.getAllDietsWithTraineeId(trainee.getId());

			if (ptService.isPtTrainerTraineeExistWithTraineeId(trainee.getId())) {
				try {
					PtTrainerTrainee ptTrainerTrainee = ptService.getPtTrainerTraineeWithTraineeId(trainee.getId());
					List<PtLesson> ptLessons = ptService.getPtLessonWithPtTrainerTrainee(ptTrainerTrainee);

					ptLessons.forEach(PtLesson::softDelete);
					ptTrainerTrainee.softDelete();
				} catch (NotFoundException e) {
					// Do nothing
				}
			}

			ptGoals.forEach(PtGoal::softDelete);

			diets.forEach(Diet::softDelete);

			trainee.softDelete();
		}

		member.softDelete();
	}
}
