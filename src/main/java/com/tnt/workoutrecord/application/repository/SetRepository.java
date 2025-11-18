package com.tnt.workoutrecord.application.repository;

import java.util.List;

import com.tnt.workoutrecord.domain.Set;

public interface SetRepository {

	List<Set> saveAll(List<Set> sets);

	List<Set> findAllByRoutineIds(List<Long> routineIds);

	void deleteAllByRoutineIds(List<Long> routineIds);
}
