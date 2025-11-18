package com.tnt.workout.presentation;

import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tnt.workout.application.WorkoutService;
import com.tnt.workout.domain.BodyPart;
import com.tnt.workout.domain.Machine;
import com.tnt.workout.dto.response.SearchWorkoutResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "운동", description = "운동 관련 API")
@RestController
@RequestMapping("/workouts")
@RequiredArgsConstructor
public class WorkoutController {

	private final WorkoutService workoutService;

	@Operation(summary = "운동 검색 API")
	@ResponseStatus(OK)
	@GetMapping("/search")
	public SearchWorkoutResponse search(
		@Parameter(description = "검색 키워드 (운동 이름)", example = "벤치")
		@RequestParam(required = false) String keyword,

		@Parameter(description = "운동 부위 목록 (복수 선택 가능)", example = "[\"CHEST\", \"SHOULDERS\"]")
		@RequestParam(required = false) List<BodyPart> bodyParts,

		@Parameter(description = "운동 기구 목록 (복수 선택 가능)", example = "[\"BARBELL\", \"DUMBBELL\"]")
		@RequestParam(required = false) List<Machine> machines,

		@Parameter(description = "마지막으로 조회한 운동 ID (커서)", example = "100")
		@RequestParam(required = false) Long lastId,

		@Parameter(description = "페이지 크기 (기본값: 10)", example = "10")
		@RequestParam(required = false) Integer size
	) {
		return workoutService.search(keyword, bodyParts, machines, lastId, size);
	}
}
