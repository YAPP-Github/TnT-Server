package com.tnt.workoutrecord.infrastructure;

import com.tnt.workoutrecord.domain.Routine;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "workout_record_routine")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoutineJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, unique = true)
	private Long id;

	@Column(name = "workout_record_id", nullable = false)
	private Long workoutRecordId;

	@Column(name = "workout_id", nullable = false)
	private Long workoutId;

	@Builder
	public RoutineJpaEntity(Long id, Long workoutRecordId, Long workoutId) {
		this.id = id;
		this.workoutRecordId = workoutRecordId;
		this.workoutId = workoutId;
	}

	public static RoutineJpaEntity from(Routine routine) {
		return RoutineJpaEntity.builder()
			.id(routine.getId())
			.workoutRecordId(routine.getWorkoutRecordId())
			.workoutId(routine.getWorkoutId())
			.build();
	}

	public Routine toModel() {
		return Routine.builder()
			.id(this.id)
			.workoutRecordId(this.workoutRecordId)
			.workoutId(this.workoutId)
			.build();
	}
}
