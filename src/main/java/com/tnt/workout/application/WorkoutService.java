package com.tnt.workout.application;

import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tnt.workout.application.repository.WorkoutRepository;
import com.tnt.workout.domain.BodyPart;
import com.tnt.workout.domain.Machine;
import com.tnt.workout.domain.Workout;
import com.tnt.workout.dto.response.SearchWorkoutResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkoutService {

	private static final int DEFAULT_PAGE_SIZE = 10;

	private final WorkoutRepository workoutRepository;

	public SearchWorkoutResponse search(@Nullable String keyword, @Nullable List<BodyPart> bodyParts,
		@Nullable List<Machine> machines, @Nullable Long lastId, @Nullable Integer size) {

		int pageSize = size != null ? size : DEFAULT_PAGE_SIZE;
		List<Workout> workouts = workoutRepository.search(keyword, bodyParts, machines, lastId, pageSize);

		// hasNext 판단을 위해 size+1 만큼 조회했으므로, 실제 반환할 데이터는 size까지만
		boolean hasNext = workouts.size() > pageSize;
		List<Workout> content = hasNext ? workouts.subList(0, pageSize) : workouts;

		return SearchWorkoutResponse.from(content, hasNext);
	}
}
