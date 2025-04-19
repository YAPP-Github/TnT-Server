package com.tnt.pt.domain;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PtLesson {

	private static final int MEMO_LENGTH = 30;

	private final Long id;
	private final PtTrainerTrainee ptTrainerTrainee;
	private final LocalDateTime lessonStart;
	private final LocalDateTime lessonEnd;
	private Boolean isCompleted;
	private String memo;
	private Integer session;
	private LocalDateTime deletedAt;

	@Builder
	public PtLesson(Long id, PtTrainerTrainee ptTrainerTrainee, LocalDateTime lessonStart, LocalDateTime lessonEnd,
		Boolean isCompleted, String memo, Integer session, LocalDateTime deletedAt) {
		this.id = id;
		this.ptTrainerTrainee = requireNonNull(ptTrainerTrainee);
		this.lessonStart = requireNonNull(lessonStart);
		this.lessonEnd = requireNonNull(lessonEnd);
		this.isCompleted = isCompleted != null && isCompleted;
		this.session = requireNonNull(session);
		this.deletedAt = deletedAt;
		validateAndSetMemo(memo);
	}

	public void complete(Integer finishedSession) {
		this.isCompleted = true;
		this.session = finishedSession;
	}

	public void cancel(Integer finishedSession) {
		this.isCompleted = false;
		this.session = finishedSession;
	}

	public void increaseSession() {
		this.session++;
	}

	public void decreaseSession() {
		this.session--;
	}

	public void softDelete() {
		this.deletedAt = LocalDateTime.now();
	}

	private void validateAndSetMemo(String memo) {
		if (memo == null) {
			return;
		}

		if (memo.length() > MEMO_LENGTH) {
			throw new IllegalArgumentException("메모는 30자 이하여야 합니다.");
		}

		this.memo = memo;
	}
}
