package com.tnt.fixture;

import java.time.LocalDate;

import com.tnt.pt.domain.PtTrainerTrainee;
import com.tnt.trainee.domain.Trainee;
import com.tnt.trainer.domain.Trainer;

public class PtTrainerTraineeFixture {

	public static PtTrainerTrainee getPtTrainerTrainee1(Trainer trainer, Trainee trainee) {
		LocalDate startDate = LocalDate.of(2025, 1, 1);
		Integer totalPtCount = 10;
		Integer finishedPtCount = 3;

		return PtTrainerTrainee.builder()
			.trainer(trainer)
			.trainee(trainee)
			.startedAt(startDate)
			.finishedPtCount(finishedPtCount)
			.totalPtCount(totalPtCount)
			.build();
	}

	public static PtTrainerTrainee getPtTrainerTrainee2(Trainer trainer, Trainee trainee) {
		LocalDate startDate = LocalDate.of(2025, 1, 10);
		Integer totalPtCount = 20;
		Integer finishedPtCount = 13;

		return PtTrainerTrainee.builder()
			.trainer(trainer)
			.trainee(trainee)
			.startedAt(startDate)
			.finishedPtCount(finishedPtCount)
			.totalPtCount(totalPtCount)
			.build();
	}

	public static PtTrainerTrainee getPtTrainerTrainee3(Trainer trainer, Trainee trainee) {
		LocalDate startDate = LocalDate.of(2025, 1, 1);
		Integer totalPtCount = 10;
		Integer finishedPtCount = 8;

		return PtTrainerTrainee.builder()
			.trainer(trainer)
			.trainee(trainee)
			.startedAt(startDate)
			.finishedPtCount(finishedPtCount)
			.totalPtCount(totalPtCount)
			.build();
	}

	public static PtTrainerTrainee getPtTrainerTrainee4(Trainer trainer, Trainee trainee) {
		LocalDate startDate = LocalDate.of(2025, 1, 1);
		Integer totalPtCount = 1;
		Integer finishedPtCount = 1;

		return PtTrainerTrainee.builder()
			.trainer(trainer)
			.trainee(trainee)
			.startedAt(startDate)
			.finishedPtCount(finishedPtCount)
			.totalPtCount(totalPtCount)
			.build();
	}
}
