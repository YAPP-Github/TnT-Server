package com.tnt.workoutrecord.domain;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
public class WorkoutRecord {

	private Long id;
	private Long memberId;
	private Long ptLessonId;
	private List<Routine> routines;
	private LocalDateTime date;
	private RecordType recordType;
	private WorkoutNote workoutNote;

	@Builder
	public WorkoutRecord(Long id, Long memberId, Long ptLessonId, List<Routine> routines, LocalDateTime date,
		RecordType recordType, WorkoutNote workoutNote) {
		this.id = id;
		this.memberId = memberId;
		this.ptLessonId = ptLessonId;
		this.routines = routines;
		this.date = date;
		this.recordType = recordType;
		this.workoutNote = workoutNote;
	}
}
