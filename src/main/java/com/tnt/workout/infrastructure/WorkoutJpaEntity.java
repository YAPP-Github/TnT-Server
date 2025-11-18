package com.tnt.workout.infrastructure;

import java.util.List;

import com.tnt.workout.domain.BodyPart;
import com.tnt.workout.domain.Machine;
import com.tnt.workout.domain.Workout;
import com.tnt.workout.domain.WorkoutType;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "workout")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkoutJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, unique = true)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "imageUrl", nullable = false)
	private String imageUrl;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "workout_body_parts", joinColumns = @JoinColumn(name = "workout_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "body_part")
	private List<BodyPart> bodyParts;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "workout_machines", joinColumns = @JoinColumn(name = "workout_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "machine")
	private List<Machine> machines;

	@Enumerated(EnumType.STRING)
	@Column(name = "workout_type", nullable = false)
	private WorkoutType workoutType;

	@Builder
	public WorkoutJpaEntity(Long id, String name, String imageUrl, List<BodyPart> bodyParts, List<Machine> machines,
		WorkoutType workoutType) {
		this.id = id;
		this.name = name;
		this.imageUrl = imageUrl;
		this.bodyParts = bodyParts;
		this.machines = machines;
		this.workoutType = workoutType;
	}

	public static WorkoutJpaEntity from(Workout workout) {
		return WorkoutJpaEntity.builder()
			.id(workout.getId())
			.name(workout.getName())
			.imageUrl(workout.getImageUrl())
			.bodyParts(workout.getBodyParts())
			.machines(workout.getMachines())
			.workoutType(workout.getWorkoutType())
			.build();
	}

	public Workout toModel() {
		return Workout.builder()
			.id(id)
			.name(name)
			.imageUrl(imageUrl)
			.bodyParts(bodyParts)
			.machines(machines)
			.workoutType(workoutType)
			.build();
	}
}
