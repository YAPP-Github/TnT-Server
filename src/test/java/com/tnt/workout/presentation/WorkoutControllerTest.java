package com.tnt.workout.presentation;

import static com.tnt.workout.domain.BodyPart.BACK;
import static com.tnt.workout.domain.BodyPart.CHEST;
import static com.tnt.workout.domain.BodyPart.LEG;
import static com.tnt.workout.domain.Machine.BARBELL;
import static com.tnt.workout.domain.Machine.DUMBBELL;
import static com.tnt.workout.domain.Machine.MACHINE;
import static com.tnt.workout.domain.WorkoutType.ANAEROBIC;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.tnt.workout.domain.BodyPart;
import com.tnt.workout.domain.Machine;
import com.tnt.workout.domain.Workout;
import com.tnt.workout.infrastructure.WorkoutJpaEntity;
import com.tnt.workout.infrastructure.WorkoutJpaRepository;

@Transactional
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc(addFilters = false)
class WorkoutControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WorkoutJpaRepository workoutJpaRepository;

	@Test
	@DisplayName("통합 테스트 - 운동 검색 성공 (키워드)")
	void search_with_keyword_success() throws Exception {
		// given
		saveWorkout("벤치프레스", List.of(CHEST), List.of(BARBELL));
		saveWorkout("인클라인벤치프레스", List.of(CHEST), List.of(BARBELL));
		saveWorkout("스쿼트", List.of(LEG), List.of(BARBELL));

		// when & then
		mockMvc.perform(get("/workouts/search")
				.param("keyword", "벤치"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.workouts").isArray())
			.andExpect(jsonPath("$.workouts.length()").value(2))
			.andExpect(jsonPath("$.hasNext").value(false));
	}

	@Test
	@DisplayName("통합 테스트 - 운동 검색 성공 (부위)")
	void search_with_bodyPart_success() throws Exception {
		// given
		saveWorkout("벤치프레스", List.of(CHEST), List.of(BARBELL));
		saveWorkout("스쿼트", List.of(LEG), List.of(BARBELL));
		saveWorkout("데드리프트", List.of(BACK), List.of(BARBELL));

		// when & then
		mockMvc.perform(get("/workouts/search")
				.param("bodyParts", "CHEST"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.workouts").isArray())
			.andExpect(jsonPath("$.workouts.length()").value(1))
			.andExpect(jsonPath("$.workouts[0].name").value("벤치프레스"))
			.andExpect(jsonPath("$.hasNext").value(false));
	}

	@Test
	@DisplayName("통합 테스트 - 운동 검색 성공 (기구)")
	void search_with_machine_success() throws Exception {
		// given
		saveWorkout("벤치프레스", List.of(CHEST), List.of(BARBELL));
		saveWorkout("덤벨프레스", List.of(CHEST), List.of(DUMBBELL));
		saveWorkout("체스트프레스", List.of(CHEST), List.of(MACHINE));

		// when & then
		mockMvc.perform(get("/workouts/search")
				.param("machines", "DUMBBELL"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.workouts").isArray())
			.andExpect(jsonPath("$.workouts.length()").value(1))
			.andExpect(jsonPath("$.workouts[0].name").value("덤벨프레스"))
			.andExpect(jsonPath("$.hasNext").value(false));
	}

	@Test
	@DisplayName("통합 테스트 - 운동 검색 성공 (복합 조건)")
	void search_with_multiple_conditions_success() throws Exception {
		// given
		saveWorkout("벤치프레스", List.of(CHEST), List.of(BARBELL));
		saveWorkout("덤벨프레스", List.of(CHEST), List.of(DUMBBELL));
		saveWorkout("스쿼트", List.of(LEG), List.of(BARBELL));

		// when & then
		mockMvc.perform(get("/workouts/search")
				.param("keyword", "프레스")
				.param("bodyParts", "CHEST")
				.param("machines", "BARBELL"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.workouts").isArray())
			.andExpect(jsonPath("$.workouts.length()").value(1))
			.andExpect(jsonPath("$.workouts[0].name").value("벤치프레스"))
			.andExpect(jsonPath("$.hasNext").value(false));
	}

	@Test
	@DisplayName("통합 테스트 - 운동 검색 성공 (페이지네이션)")
	void search_with_pagination_success() throws Exception {
		// given
		for (int i = 1; i <= 15; i++) {
			saveWorkout("운동" + i, List.of(CHEST), List.of(BARBELL));
		}

		// when & then - 첫 페이지
		mockMvc.perform(get("/workouts/search")
				.param("size", "10"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.workouts").isArray())
			.andExpect(jsonPath("$.workouts.length()").value(10))
			.andExpect(jsonPath("$.hasNext").value(true));
	}

	@Test
	@DisplayName("통합 테스트 - 운동 검색 성공 (커서 페이지네이션)")
	void search_with_cursor_pagination_success() throws Exception {
		// given
		List<WorkoutJpaEntity> savedWorkouts = List.of(
			saveWorkout("운동1", List.of(CHEST), List.of(BARBELL)),
			saveWorkout("운동2", List.of(CHEST), List.of(BARBELL)),
			saveWorkout("운동3", List.of(CHEST), List.of(BARBELL)),
			saveWorkout("운동4", List.of(CHEST), List.of(BARBELL)),
			saveWorkout("운동5", List.of(CHEST), List.of(BARBELL))
		);

		Long lastId = savedWorkouts.get(2).getId(); // 운동3의 ID

		// when & then - 커서 기반 다음 페이지
		mockMvc.perform(get("/workouts/search")
				.param("lastId", lastId.toString())
				.param("size", "10"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.workouts").isArray())
			.andExpect(jsonPath("$.hasNext").value(false));
	}

	@Test
	@DisplayName("통합 테스트 - 운동 검색 결과 없음")
	void search_no_results() throws Exception {
		// when & then
		mockMvc.perform(get("/workouts/search")
				.param("keyword", "존재하지않는운동"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.workouts").isArray())
			.andExpect(jsonPath("$.workouts.length()").value(0))
			.andExpect(jsonPath("$.hasNext").value(false));
	}

	private WorkoutJpaEntity saveWorkout(String name, List<BodyPart> bodyParts, List<Machine> machines) {
		Workout workout = Workout.builder()
			.name(name)
			.imageUrl("https://images.tntapp.co.kr/" + name + ".jpg")
			.bodyParts(bodyParts)
			.machines(machines)
			.workoutType(ANAEROBIC)
			.build();

		return workoutJpaRepository.save(WorkoutJpaEntity.from(workout));
	}
}
