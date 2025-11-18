package com.tnt.workoutrecord.infrastructure;

import com.tnt.workoutrecord.domain.Set;

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
@Table(name = "workout_record_set")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SetJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, unique = true)
	private Long id;

	@Column(name = "routine_id", nullable = false)
	private Long routineId;

	@Column(name = "duration_minutes", nullable = true)
	private Integer durationMinutes;

	@Column(name = "repetition", nullable = true)
	private Integer repetition;

	@Column(name = "weight", nullable = true)
	private Integer weight;

	@Builder
	public SetJpaEntity(Long id, Long routineId, Integer durationMinutes, Integer repetition,
		Integer weight) {
		this.id = id;
		this.routineId = routineId;
		this.durationMinutes = durationMinutes;
		this.repetition = repetition;
		this.weight = weight;
	}

	public static SetJpaEntity from(Set set) {
		return SetJpaEntity.builder()
			.id(set.getId())
			.routineId(set.getRoutineId())
			.durationMinutes(set.getDurationMinutes())
			.repetition(set.getRepetition())
			.weight(set.getWeight())
			.build();
	}

	public Set toModel() {
		return Set.builder()
			.id(this.id)
			.routineId(this.routineId)
			.durationMinutes(this.durationMinutes)
			.repetition(this.repetition)
			.weight(this.weight)
			.build();
	}
}
