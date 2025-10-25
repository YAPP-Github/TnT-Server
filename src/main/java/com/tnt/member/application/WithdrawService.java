package com.tnt.member.application;

import static com.tnt.member.domain.MemberType.TRAINEE;
import static com.tnt.member.domain.MemberType.TRAINER;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tnt.common.error.exception.NotFoundException;
import com.tnt.gateway.application.SessionService;
import com.tnt.member.application.repository.MemberRepository;
import com.tnt.member.domain.Member;
import com.tnt.member.dto.WithdrawDto;
import com.tnt.pt.application.PtService;
import com.tnt.pt.application.repository.PtLessonRepository;
import com.tnt.pt.application.repository.PtTrainerTraineeRepository;
import com.tnt.pt.domain.PtLesson;
import com.tnt.pt.domain.PtTrainerTrainee;
import com.tnt.trainee.application.DietService;
import com.tnt.trainee.application.TraineeService;
import com.tnt.trainee.application.repository.DietRepository;
import com.tnt.trainee.application.repository.TraineeRepository;
import com.tnt.trainee.domain.Diet;
import com.tnt.trainee.domain.Trainee;
import com.tnt.trainer.application.TrainerService;
import com.tnt.trainer.application.repository.TrainerRepository;
import com.tnt.trainer.domain.Trainer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WithdrawService {

	private final SessionService sessionService;
	private final MemberService memberService;
	private final TrainerService trainerService;
	private final TraineeService traineeService;
	private final DietService dietService;
	private final PtService ptService;
	private final MemberRepository memberRepository;
	private final TrainerRepository trainerRepository;
	private final TraineeRepository traineeRepository;
	private final PtLessonRepository ptLessonRepository;
	private final PtTrainerTraineeRepository ptTrainerTraineeRepository;
	private final DietRepository dietRepository;

	@Transactional
	public WithdrawDto withdraw(Long memberId) {
		Member member = memberService.getByMemberId(memberId);

		deleteMemberData(member);

		sessionService.removeSession(String.valueOf(memberId));

		return new WithdrawDto(member.getSocialId(), member.getSocialType(), member.getProfileImageUrl());
	}

	private void deleteMemberData(Member member) {
		if (member.getMemberType() == TRAINER) {
			Trainer trainer = trainerService.getByMemberId(member.getId());

			if (ptService.isPtTrainerTraineeExistWithTrainerId(trainer.getId())) {
				List<PtTrainerTrainee> ptTrainerTrainees = ptService.getAllPtTrainerTraineeWithTrainerId(
					trainer.getId());

				List<PtLesson> ptLessons = ptTrainerTrainees.stream()
					.map(ptService::getPtLessonWithPtTrainerTrainee)
					.flatMap(List::stream)
					.toList();

				ptLessons.forEach(PtLesson::softDelete);
				ptTrainerTrainees.forEach(PtTrainerTrainee::softDelete);

				ptLessonRepository.saveAll(ptLessons);
				ptTrainerTraineeRepository.saveAll(ptTrainerTrainees);
			}

			trainer.softDelete();
			trainerRepository.save(trainer);
		}

		if (member.getMemberType() == TRAINEE) {
			Trainee trainee = traineeService.getByMemberId(member.getId());
			List<Diet> diets = dietService.getAllByTraineeId(trainee.getId());

			if (ptService.isPtTrainerTraineeExistWithTraineeId(trainee.getId())) {
				try {
					PtTrainerTrainee ptTrainerTrainee = ptService.getPtTrainerTraineeWithTraineeId(trainee.getId());
					List<PtLesson> ptLessons = ptService.getPtLessonWithPtTrainerTrainee(ptTrainerTrainee);

					ptLessons.forEach(PtLesson::softDelete);
					ptTrainerTrainee.softDelete();

					ptLessonRepository.saveAll(ptLessons);
					ptTrainerTraineeRepository.save(ptTrainerTrainee);
				} catch (NotFoundException e) {
					// Do nothing
				}
			}

			diets.forEach(Diet::softDelete);
			dietRepository.saveAll(diets);

			trainee.softDelete();
			traineeRepository.save(trainee);
		}

		member.softDelete();
		memberRepository.save(member);
	}
}
