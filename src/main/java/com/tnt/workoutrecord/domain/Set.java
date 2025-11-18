package com.tnt.workoutrecord.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Set {

	private Long id;
	private Long routineId;
	private Integer durationMinutes;
	private Integer repetition;
	private Integer weight;

	@Builder
	public Set(Long id, Long routineId, Integer durationMinutes, Integer repetition, Integer weight) {
		this.id = id;
		this.routineId = routineId;
		this.durationMinutes = durationMinutes;
		this.repetition = repetition;
		this.weight = weight;
	}
}
