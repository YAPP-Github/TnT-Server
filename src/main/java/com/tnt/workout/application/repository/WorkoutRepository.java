package com.tnt.workout.application.repository;

import java.util.List;

import org.springframework.lang.Nullable;

import com.tnt.workout.domain.BodyPart;
import com.tnt.workout.domain.Machine;
import com.tnt.workout.domain.Workout;

public interface WorkoutRepository {

	List<Workout> search(@Nullable String keyword, @Nullable List<BodyPart> bodyParts,
		@Nullable List<Machine> machines, @Nullable Long lastId, int size);
}
