package com.tnt.workoutrecord.domain;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Routine {

	private Long id;
	private Long workoutRecordId;
	private Long workoutId;
	private List<Set> sets;

	@Builder
	public Routine(Long id, Long workoutRecordId, Long workoutId, List<Set> sets) {
		this.id = id;
		this.workoutRecordId = workoutRecordId;
		this.workoutId = workoutId;
		this.sets = sets;
	}
}
