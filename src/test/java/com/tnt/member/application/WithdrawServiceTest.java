package com.tnt.member.application;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tnt.common.error.exception.NotFoundException;
import com.tnt.fixture.DietFixture;
import com.tnt.fixture.MemberFixture;
import com.tnt.fixture.PtLessonsFixture;
import com.tnt.fixture.PtTrainerTraineeFixture;
import com.tnt.fixture.TraineeFixture;
import com.tnt.fixture.TrainerFixture;
import com.tnt.gateway.application.SessionService;
import com.tnt.member.application.repository.MemberRepository;
import com.tnt.member.domain.Member;
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

@ExtendWith(MockitoExtension.class)
class WithdrawServiceTest {

	@Mock
	private SessionService sessionService;

	@Mock
	private MemberService memberService;

	@Mock
	private TrainerService trainerService;

	@Mock
	private TraineeService traineeService;

	@Mock
	private DietService dietService;

	@Mock
	private PtService ptService;

	@Mock
	private TraineeRepository traineeRepository;

	@Mock
	private TrainerRepository trainerRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PtTrainerTraineeRepository ptTrainerTraineeRepository;

	@Mock
	private PtLessonRepository ptLessonRepository;

	@Mock
	private DietRepository dietRepository;

	@InjectMocks
	private WithdrawService withdrawService;

	@Test
	@DisplayName("트레이너 회원 탈퇴 성공")
	void withdraw_trainer_success() {
		// given
		Member trainerMember = MemberFixture.getTrainerMemberWithId1();
		Trainer trainer = TrainerFixture.getTrainerWithId1(trainerMember);

		given(memberService.getByMemberId(trainerMember.getId())).willReturn(trainerMember);
		given(trainerService.getByMemberId(trainerMember.getId())).willReturn(trainer);

		// when
		withdrawService.withdraw(trainerMember.getId());

		// then
		verify(sessionService).removeSession(String.valueOf(trainerMember.getId()));
	}

	@Test
	@DisplayName("트레이니 회원 탈퇴 성공")
	void withdraw_trainee_success() {
		// given
		Member traineeMember = MemberFixture.getTraineeMemberWithId1();
		Trainee trainee = TraineeFixture.getTrainee1WithId(traineeMember);
		List<Diet> diets = List.of(DietFixture.getDiet1(trainee.getId()), DietFixture.getDiet2(trainee.getId()));

		given(memberService.getByMemberId(traineeMember.getId())).willReturn(traineeMember);
		given(traineeService.getByMemberId(traineeMember.getId())).willReturn(trainee);
		given(dietService.getAllByTraineeId(trainee.getId())).willReturn(diets);
		given(dietRepository.saveAll(diets)).willReturn(diets);

		// when
		withdrawService.withdraw(traineeMember.getId());

		// then
		verify(sessionService).removeSession(String.valueOf(traineeMember.getId()));
	}

	@Test
	@DisplayName("PT 관계가 있는 트레이너 회원 탈퇴 성공")
	void withdraw_trainer_with_pt_success() {
		// given
		Member trainerMember = MemberFixture.getTrainerMemberWithId1();
		Member traineeMember = MemberFixture.getTraineeMemberWithId2();

		Trainer trainer = TrainerFixture.getTrainerWithId1(trainerMember);
		Trainee trainee = TraineeFixture.getTrainee1WithId(traineeMember);

		PtTrainerTrainee ptTrainerTrainee = PtTrainerTraineeFixture.getPtTrainerTrainee1(trainer, trainee);

		List<PtLesson> ptLessons = PtLessonsFixture.getPtLessonsWithId1(ptTrainerTrainee);

		given(memberService.getByMemberId(trainerMember.getId())).willReturn(trainerMember);
		given(trainerService.getByMemberId(trainerMember.getId())).willReturn(trainer);
		given(ptService.isPtTrainerTraineeExistWithTrainerId(trainer.getId())).willReturn(true);
		given(ptService.getAllPtTrainerTraineeWithTrainerId(trainer.getId())).willReturn(List.of(ptTrainerTrainee));
		given(ptService.getPtLessonWithPtTrainerTrainee(ptTrainerTrainee)).willReturn(ptLessons);

		// when
		withdrawService.withdraw(trainerMember.getId());

		// then
		verify(sessionService).removeSession(String.valueOf(trainerMember.getId()));
	}

	@Test
	@DisplayName("PT 관계가 있는 트레이니 회원 탈퇴 성공")
	void withdraw_trainee_with_pt_success() {
		// given
		Member trainerMember = MemberFixture.getTrainerMemberWithId1();
		Member traineeMember = MemberFixture.getTraineeMemberWithId1();

		Trainer trainer = TrainerFixture.getTrainerWithId1(trainerMember);
		Trainee trainee = TraineeFixture.getTrainee1WithId(traineeMember);

		PtTrainerTrainee ptTrainerTrainee = PtTrainerTraineeFixture.getPtTrainerTrainee1(trainer, trainee);

		List<Diet> diets = List.of(DietFixture.getDiet1(trainee.getId()),
			DietFixture.getDiet2(trainee.getId()));

		List<PtLesson> ptLessons = PtLessonsFixture.getPtLessonsWithId1(ptTrainerTrainee);

		given(memberService.getByMemberId(traineeMember.getId())).willReturn(traineeMember);
		given(traineeService.getByMemberId(traineeMember.getId())).willReturn(trainee);
		given(ptService.isPtTrainerTraineeExistWithTraineeId(trainee.getId())).willReturn(true);
		given(dietService.getAllByTraineeId(trainee.getId())).willReturn(diets);
		given(ptService.getPtTrainerTraineeWithTraineeId(trainee.getId())).willReturn(ptTrainerTrainee);
		given(ptService.getPtLessonWithPtTrainerTrainee(ptTrainerTrainee)).willReturn(ptLessons);
		given(dietRepository.saveAll(diets)).willReturn(diets);

		// when
		withdrawService.withdraw(traineeMember.getId());

		// then
		verify(sessionService).removeSession(String.valueOf(traineeMember.getId()));
	}

	@Test
	@DisplayName("PT 관계가 없는 트레이너 회원 탈퇴 성공")
	void withdraw_trainer_without_pt_success() {
		// given
		Member trainerMember = MemberFixture.getTrainerMemberWithId1();

		Trainer trainer = TrainerFixture.getTrainerWithId1(trainerMember);

		given(memberService.getByMemberId(trainerMember.getId())).willReturn(trainerMember);
		given(trainerService.getByMemberId(trainerMember.getId())).willReturn(trainer);
		given(ptService.isPtTrainerTraineeExistWithTrainerId(trainer.getId())).willReturn(true);
		given(trainerRepository.save(trainer)).willReturn(trainer);

		willDoNothing().given(ptTrainerTraineeRepository).saveAll(anyList());
		willDoNothing().given(ptLessonRepository).saveAll(anyList());

		// when
		withdrawService.withdraw(trainerMember.getId());

		// then
		verify(sessionService).removeSession(String.valueOf(trainerMember.getId()));
	}

	@Test
	@DisplayName("PT 관계가 없는 트레이니 회원 탈퇴 성공")
	void withdraw_trainee_without_pt_success() {
		// given
		Member traineeMember = MemberFixture.getTraineeMemberWithId1();

		Trainee trainee = TraineeFixture.getTrainee1WithId(traineeMember);

		List<Diet> diets = List.of(DietFixture.getDiet1(trainee.getId()),
			DietFixture.getDiet2(trainee.getId()));

		given(memberService.getByMemberId(traineeMember.getId())).willReturn(traineeMember);
		given(traineeService.getByMemberId(traineeMember.getId())).willReturn(trainee);
		given(ptService.isPtTrainerTraineeExistWithTraineeId(trainee.getId())).willReturn(true);
		given(dietService.getAllByTraineeId(trainee.getId())).willReturn(diets);
		given(ptService.getPtTrainerTraineeWithTraineeId(trainee.getId())).willThrow(NotFoundException.class);
		given(traineeRepository.save(trainee)).willReturn(trainee);
		given(dietRepository.saveAll(diets)).willReturn(diets);
		given(memberRepository.save(traineeMember)).willReturn(traineeMember);

		// when
		withdrawService.withdraw(traineeMember.getId());

		// then
		verify(sessionService).removeSession(String.valueOf(traineeMember.getId()));
	}
}
