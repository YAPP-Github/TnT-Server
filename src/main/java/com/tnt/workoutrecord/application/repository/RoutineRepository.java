package com.tnt.workoutrecord.application.repository;

import java.util.List;

import com.tnt.workoutrecord.domain.Routine;

public interface RoutineRepository {

	List<Routine> saveAll(List<Routine> routines);

	List<Routine> findAllByWorkoutRecordId(Long workoutRecordId);

	void deleteAllByWorkoutRecordId(Long workoutRecordId);
}
