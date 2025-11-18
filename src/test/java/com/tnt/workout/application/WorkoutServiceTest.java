package com.tnt.workout.application;

import static com.tnt.workout.domain.BodyPart.CHEST;
import static com.tnt.workout.domain.BodyPart.LEG;
import static com.tnt.workout.domain.BodyPart.SHOULDERS;
import static com.tnt.workout.domain.Machine.BARBELL;
import static com.tnt.workout.domain.Machine.MACHINE;
import static com.tnt.workout.domain.WorkoutType.ANAEROBIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tnt.workout.application.repository.WorkoutRepository;
import com.tnt.workout.domain.BodyPart;
import com.tnt.workout.domain.Machine;
import com.tnt.workout.domain.Workout;
import com.tnt.workout.dto.response.SearchWorkoutResponse;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

	@InjectMocks
	private WorkoutService workoutService;

	@Mock
	private WorkoutRepository workoutRepository;

	@Test
	@DisplayName("운동 검색 - 키워드로 검색 성공")
	void search_with_keyword_success() {
		// given
		String keyword = "벤치";
		List<Workout> workouts = List.of(createWorkout(1L, "벤치프레스", List.of(CHEST), List.of(BARBELL)),
			createWorkout(2L, "인클라인벤치프레스", List.of(CHEST, SHOULDERS), List.of(BARBELL)));

		given(workoutRepository.search(keyword, null, null, null, 10)).willReturn(workouts);

		// when
		SearchWorkoutResponse response = workoutService.search(keyword, null, null, null, 10);

		// then
		assertThat(response.workouts()).hasSize(2);
		assertThat(response.hasNext()).isFalse();
	}

	@Test
	@DisplayName("운동 검색 - 부위로 검색 성공")
	void search_with_bodyParts_success() {
		// given
		List<BodyPart> bodyParts = List.of(CHEST);
		List<Workout> workouts = List.of(createWorkout(1L, "벤치프레스", List.of(CHEST), List.of(BARBELL)),
			createWorkout(2L, "체스트프레스", List.of(CHEST), List.of(MACHINE)));

		given(workoutRepository.search(null, bodyParts, null, null, 10)).willReturn(workouts);

		// when
		SearchWorkoutResponse response = workoutService.search(null, bodyParts, null, null, 10);

		// then
		assertThat(response.workouts()).hasSize(2);
		assertThat(response.hasNext()).isFalse();
	}

	@Test
	@DisplayName("운동 검색 - 기구로 검색 성공")
	void search_with_machines_success() {
		// given
		List<Machine> machines = List.of(BARBELL);
		List<Workout> workouts = List.of(
			createWorkout(1L, "벤치프레스", List.of(CHEST), List.of(BARBELL)),
			createWorkout(2L, "스쿼트", List.of(LEG), List.of(BARBELL)));

		given(workoutRepository.search(null, null, machines, null, 10)).willReturn(workouts);

		// when
		SearchWorkoutResponse response = workoutService.search(null, null, machines, null, 10);

		// then
		assertThat(response.workouts()).hasSize(2);
		assertThat(response.hasNext()).isFalse();
	}

	@Test
	@DisplayName("운동 검색 - 복합 조건 검색 성공")
	void search_with_multiple_conditions_success() {
		// given
		String keyword = "프레스";
		List<BodyPart> bodyParts = List.of(CHEST);
		List<Machine> machines = List.of(BARBELL);

		List<Workout> workouts = List.of(createWorkout(1L, "벤치프레스", List.of(CHEST), List.of(BARBELL)));

		given(workoutRepository.search(keyword, bodyParts, machines, null, 10)).willReturn(workouts);

		// when
		SearchWorkoutResponse response = workoutService.search(keyword, bodyParts, machines, null, 10);

		// then
		assertThat(response.workouts()).hasSize(1);
		assertThat(response.workouts().getFirst().name()).isEqualTo("벤치프레스");
		assertThat(response.hasNext()).isFalse();
	}

	@Test
	@DisplayName("운동 검색 - 페이지네이션 hasNext true")
	void search_with_pagination_hasNext_true() {
		// given
		int pageSize = 10;

		List<Workout> workouts = List.of(
			createWorkout(11L, "운동11", List.of(CHEST), List.of(BARBELL)),
			createWorkout(10L, "운동10", List.of(CHEST), List.of(BARBELL)),
			createWorkout(9L, "운동9", List.of(CHEST), List.of(BARBELL)),
			createWorkout(8L, "운동8", List.of(CHEST), List.of(BARBELL)),
			createWorkout(7L, "운동7", List.of(CHEST), List.of(BARBELL)),
			createWorkout(6L, "운동6", List.of(CHEST), List.of(BARBELL)),
			createWorkout(5L, "운동5", List.of(CHEST), List.of(BARBELL)),
			createWorkout(4L, "운동4", List.of(CHEST), List.of(BARBELL)),
			createWorkout(3L, "운동3", List.of(CHEST), List.of(BARBELL)),
			createWorkout(2L, "운동2", List.of(CHEST), List.of(BARBELL)),
			createWorkout(1L, "운동1", List.of(CHEST), List.of(BARBELL))
		);

		given(workoutRepository.search(null, null, null, null, pageSize)).willReturn(workouts);

		// when
		SearchWorkoutResponse response = workoutService.search(null, null, null, null, pageSize);

		// then
		assertThat(response.workouts()).hasSize(pageSize);
		assertThat(response.hasNext()).isTrue();
	}

	@Test
	@DisplayName("운동 검색 - 커서 페이지네이션")
	void search_with_cursor_pagination() {
		// given
		Long lastId = 100L;
		List<Workout> workouts = List.of(
			createWorkout(99L, "운동99", List.of(CHEST), List.of(BARBELL)),
			createWorkout(98L, "운동98", List.of(CHEST), List.of(BARBELL))
		);

		given(workoutRepository.search(null, null, null, lastId, 10)).willReturn(workouts);

		// when
		SearchWorkoutResponse response = workoutService.search(null, null, null, lastId, 10);

		// then
		assertThat(response.workouts()).hasSize(2);
		assertThat(response.workouts().getFirst().workoutId()).isEqualTo(99L);
		assertThat(response.hasNext()).isFalse();
	}

	@Test
	@DisplayName("운동 검색 - 결과 없음")
	void search_no_results() {
		// given
		given(workoutRepository.search(null, null, null, null, 10)).willReturn(List.of());

		// when
		SearchWorkoutResponse response = workoutService.search(null, null, null, null, 10);

		// then
		assertThat(response.workouts()).isEmpty();
		assertThat(response.hasNext()).isFalse();
	}

	private Workout createWorkout(Long id, String name, List<BodyPart> bodyParts, List<Machine> machines) {
		return Workout.builder()
			.id(id)
			.name(name)
			.imageUrl("https://images.tntapp.co.kr/" + name + ".jpg")
			.bodyParts(bodyParts)
			.machines(machines)
			.workoutType(ANAEROBIC)
			.build();
	}
}
