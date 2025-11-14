package com.tnt.workoutrecord.infrastructure;

import java.time.LocalDateTime;

import com.tnt.workoutrecord.domain.RecordType;
import com.tnt.workoutrecord.domain.WorkoutNote;
import com.tnt.workoutrecord.domain.WorkoutRecord;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "workout_record")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkoutRecordJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, unique = true)
	private Long id;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Column(name = "pt_lesson_id", nullable = true)
	private Long ptLessonId;

	@Column(name = "date", nullable = false)
	private LocalDateTime date;

	@Embedded
	private WorkoutNote workoutNote;

	@Enumerated(EnumType.STRING)
	@Column(name = "record_type", nullable = false)
	private RecordType recordType;

	@Builder
	public WorkoutRecordJpaEntity(Long id, Long memberId, Long ptLessonId, LocalDateTime date, WorkoutNote workoutNote,
		RecordType recordType) {
		this.id = id;
		this.memberId = memberId;
		this.ptLessonId = ptLessonId;
		this.date = date;
		this.workoutNote = workoutNote;
		this.recordType = recordType;
	}

	public static WorkoutRecordJpaEntity from(WorkoutRecord workoutRecord) {
		return WorkoutRecordJpaEntity.builder()
			.id(workoutRecord.getId())
			.memberId(workoutRecord.getMemberId())
			.ptLessonId(workoutRecord.getPtLessonId())
			.date(workoutRecord.getDate())
			.workoutNote(workoutRecord.getWorkoutNote())
			.recordType(workoutRecord.getRecordType())
			.build();
	}

	public WorkoutRecord toModel() {
		return WorkoutRecord.builder()
			.id(this.id)
			.memberId(this.memberId)
			.ptLessonId(this.ptLessonId)
			.date(this.date)
			.workoutNote(this.workoutNote)
			.recordType(this.recordType)
			.build();
	}
}
