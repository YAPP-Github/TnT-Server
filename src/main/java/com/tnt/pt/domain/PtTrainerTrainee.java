package com.tnt.pt.domain;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.tnt.trainee.domain.Trainee;
import com.tnt.trainer.domain.Trainer;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PtTrainerTrainee {

	private final Long id;
	private final Trainer trainer;
	private final Trainee trainee;
	private final LocalDate startedAt;
	private final Integer totalPtCount;
	private Integer finishedPtCount;
	private LocalDateTime deletedAt;

	@Builder
	public PtTrainerTrainee(Long id, Trainer trainer, Trainee trainee, LocalDate startedAt, Integer finishedPtCount,
		Integer totalPtCount, LocalDateTime deletedAt) {
		this.id = id;
		this.trainer = requireNonNull(trainer);
		this.trainee = requireNonNull(trainee);
		this.startedAt = requireNonNull(startedAt);
		this.finishedPtCount = requireNonNull(finishedPtCount);
		this.totalPtCount = requireNonNull(totalPtCount);
		this.deletedAt = deletedAt;
	}

	public int getCurrentPtSession() {
		return this.finishedPtCount + 1;
	}

	public void completeLesson() {
		this.finishedPtCount++;
	}

	public void cancelLesson() {
		this.finishedPtCount--;
	}

	public void softDelete() {
		this.deletedAt = LocalDateTime.now();
	}
}
