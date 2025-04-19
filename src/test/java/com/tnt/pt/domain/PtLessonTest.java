package com.tnt.pt.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tnt.fixture.MemberFixture;
import com.tnt.fixture.PtTrainerTraineeFixture;
import com.tnt.fixture.TraineeFixture;
import com.tnt.fixture.TrainerFixture;
import com.tnt.member.domain.Member;
import com.tnt.trainee.domain.Trainee;
import com.tnt.trainer.domain.Trainer;

class PtLessonTest {

	@Test
	@DisplayName("길이가 30을 넘은 메모 생성 실패")
	void create_memo_over_length_fail() {
		// given
		String failMemo = "123456789012345678901234567890149238749823479823479734239874";

		Member trainerMember = MemberFixture.getTrainerMember1();
		Member traineeMember = MemberFixture.getTraineeMember1();

		Trainer trainer = TrainerFixture.getTrainerWithId1(trainerMember);
		Trainee trainee = TraineeFixture.getTrainee1WithId(traineeMember);

		PtTrainerTrainee ptTrainerTrainee = PtTrainerTraineeFixture.getPtTrainerTrainee1(trainer, trainee);

		//when & then
		assertThatThrownBy(() -> PtLesson.builder()
			.ptTrainerTrainee(ptTrainerTrainee)
			.session(1)
			.lessonStart(LocalDateTime.of(2021, 1, 1, 10, 0))
			.lessonEnd(LocalDateTime.of(2021, 1, 1, 11, 0))
			.memo(failMemo)
			.build()
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("회차 증가 성공")
	void increase_session_success() {
		// given
		String memo = "메모";
		Member trainerMember = MemberFixture.getTrainerMember1();
		Member traineeMember = MemberFixture.getTraineeMember1();

		Trainer trainer = TrainerFixture.getTrainerWithId1(trainerMember);
		Trainee trainee = TraineeFixture.getTrainee1WithId(traineeMember);

		PtTrainerTrainee ptTrainerTrainee = PtTrainerTraineeFixture.getPtTrainerTrainee1(trainer, trainee);

		PtLesson ptLesson = PtLesson.builder()
			.ptTrainerTrainee(ptTrainerTrainee)
			.session(1)
			.lessonStart(LocalDateTime.of(2021, 1, 1, 10, 0))
			.lessonEnd(LocalDateTime.of(2021, 1, 1, 11, 0))
			.memo(memo)
			.build();

		ptLesson.increaseSession();

		assertThat(ptLesson.getSession()).isEqualTo(2);
	}

	@Test
	@DisplayName("회차 감소 성공")
	void decrease_session_success() {
		// given
		String memo = "메모";
		Member trainerMember = MemberFixture.getTrainerMember1();
		Member traineeMember = MemberFixture.getTraineeMember1();

		Trainer trainer = TrainerFixture.getTrainerWithId1(trainerMember);
		Trainee trainee = TraineeFixture.getTrainee1WithId(traineeMember);

		PtTrainerTrainee ptTrainerTrainee = PtTrainerTraineeFixture.getPtTrainerTrainee1(trainer, trainee);

		PtLesson ptLesson = PtLesson.builder()
			.ptTrainerTrainee(ptTrainerTrainee)
			.session(2)
			.lessonStart(LocalDateTime.of(2021, 1, 1, 10, 0))
			.lessonEnd(LocalDateTime.of(2021, 1, 1, 11, 0))
			.memo(memo)
			.build();

		ptLesson.decreaseSession();

		assertThat(ptLesson.getSession()).isEqualTo(1);
	}
}
