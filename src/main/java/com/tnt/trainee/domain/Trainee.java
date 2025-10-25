package com.tnt.trainee.domain;

import static com.tnt.common.error.model.ErrorMessage.TRAINEE_INVALID_CAUTION_NOTE;
import static java.util.Objects.isNull;

import java.time.LocalDateTime;
import java.util.List;

import com.tnt.member.domain.Member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Trainee {

	public static final int CAUTION_NOTE_LENGTH = 100;

	private final Long id;
	private final Member member;
	private Double height;
	private Double weight;
	private String cautionNote;
	private List<PtGoal> ptGoals;
	private LocalDateTime deletedAt;

	@Builder
	public Trainee(Long id, Member member, Double height, Double weight, String cautionNote, List<PtGoal> ptGoals,
		LocalDateTime deletedAt) {
		this.id = id;
		this.member = member;
		this.height = height;
		this.weight = weight;
		this.ptGoals = ptGoals;
		this.deletedAt = deletedAt;
		validateAndSetCautionNote(cautionNote);
	}

	public void updatePtGoals(List<PtGoal> ptGoals) {
		this.ptGoals = ptGoals;
	}

	public void updateTraineeInfo(Double height, Double weight, String cautionNote, List<PtGoal> ptGoals) {
		this.height = height;
		this.weight = weight;
		validateAndSetCautionNote(cautionNote);
		this.ptGoals = ptGoals;
	}

	private void validateAndSetCautionNote(String cautionNote) {
		if (isNull(cautionNote)) {
			return;
		}

		if (cautionNote.length() > CAUTION_NOTE_LENGTH) {
			throw new IllegalArgumentException(TRAINEE_INVALID_CAUTION_NOTE.getMessage());
		}

		this.cautionNote = cautionNote;
	}

	public void softDelete() {
		this.deletedAt = LocalDateTime.now();
	}
}
