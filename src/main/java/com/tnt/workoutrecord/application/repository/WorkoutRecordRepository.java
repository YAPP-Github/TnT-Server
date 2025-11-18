package com.tnt.workoutrecord.application.repository;

import com.tnt.workoutrecord.domain.WorkoutRecord;

public interface WorkoutRecordRepository {

	WorkoutRecord save(WorkoutRecord workoutRecord);

	WorkoutRecord findById(Long workoutRecordId);

	void deleteById(Long workoutRecordId);
}
