package com.tnt.workoutrecord.presentation;

import static com.tnt.workoutrecord.domain.RecordType.TRAINEE;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.tnt.fixture.MemberFixture;
import com.tnt.gateway.filter.CustomUserDetails;
import com.tnt.member.application.repository.MemberRepository;
import com.tnt.member.domain.Member;
import com.tnt.workoutrecord.application.repository.RoutineRepository;
import com.tnt.workoutrecord.application.repository.SetRepository;
import com.tnt.workoutrecord.application.repository.WorkoutRecordRepository;
import com.tnt.workoutrecord.domain.Routine;
import com.tnt.workoutrecord.domain.Set;
import com.tnt.workoutrecord.domain.WorkoutNote;
import com.tnt.workoutrecord.domain.WorkoutRecord;

@Transactional
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc(addFilters = false)
class WorkoutRecordControllerTest {

	private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private WorkoutRecordRepository workoutRecordRepository;

	@Autowired
	private RoutineRepository routineRepository;

	@Autowired
	private SetRepository setRepository;

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	@DisplayName("통합 테스트 - 운동 기록 등록 성공")
	void create_workout_record_with_images_success() throws Exception {
		// given
		Member member = MemberFixture.getTraineeMember1();
		member = memberRepository.save(member);

		setAuthentication(member);

		String requestJson = """
			{
				"ptLessonId": 100,
				"date": "2025-01-15T14:30:00",
				"recordType": "TRAINEE",
				"routines": [
					{
						"workoutId": 1,
						"sets": [
							{"durationMinutes": null, "repetition": 12, "weight": 50},
							{"durationMinutes": null, "repetition": 10, "weight": 50}
						]
					},
					{
						"workoutId": 2,
						"sets": [
							{"durationMinutes": 30, "repetition": null, "weight": null}
						]
					}
				],
				"feedback": "오늘 운동 강도가 적절했습니다."
			}
			""";

		MockMultipartFile image1 = new MockMultipartFile("images", "workout1.jpg", IMAGE_JPEG_VALUE,
			"workout image 1".getBytes());
		MockMultipartFile image2 = new MockMultipartFile("images", "workout2.jpg", IMAGE_JPEG_VALUE,
			"workout image 2".getBytes());

		// when
		var jsonRequest = new MockMultipartFile("request", "", APPLICATION_JSON_VALUE,
			requestJson.getBytes());

		var result = mockMvc.perform(multipart("/workout-records")
			.file(jsonRequest)
			.file(image1)
			.file(image2)
			.contentType(MULTIPART_FORM_DATA_VALUE));

		// then
		result.andExpect(status().isCreated())
			.andDo(print());
	}

	@Test
	@DisplayName("통합 테스트 - 이미지 없이 운동 기록 등록 성공")
	void create_workout_record_without_images_success() throws Exception {
		// given
		Member member = MemberFixture.getTraineeMember2();
		member = memberRepository.save(member);

		setAuthentication(member);

		String requestJson = """
			{
				"ptLessonId": null,
				"date": "2025-01-16T10:00:00",
				"recordType": "TRAINEE",
				"routines": [
					{
						"workoutId": 3,
						"sets": [
							{"durationMinutes": null, "repetition": 15, "weight": 60}
						]
					}
				],
				"feedback": null
			}
			""";

		// when
		var jsonRequest = new MockMultipartFile("request", "", APPLICATION_JSON_VALUE,
			requestJson.getBytes());

		var result = mockMvc.perform(multipart("/workout-records")
			.file(jsonRequest)
			.contentType(MULTIPART_FORM_DATA_VALUE));

		// then
		result.andExpect(status().isCreated())
			.andDo(print());
	}

	@Test
	@DisplayName("통합 테스트 - PT 운동 기록 등록 성공")
	void create_workout_record_pt_type_success() throws Exception {
		// given
		Member member = MemberFixture.getTraineeMember1();
		member = memberRepository.save(member);

		setAuthentication(member);

		String requestJson = """
			{
				"ptLessonId": 200,
				"date": "2025-01-17T16:00:00",
				"recordType": "PT",
				"routines": [
					{
						"workoutId": 10,
						"sets": [
							{"durationMinutes": null, "repetition": 12, "weight": 40},
							{"durationMinutes": null, "repetition": 10, "weight": 40},
							{"durationMinutes": null, "repetition": 8, "weight": 45}
						]
					}
				],
				"feedback": "강도를 점차 높여가고 있습니다."
			}
			""";

		MockMultipartFile image = new MockMultipartFile("images", "pt_record.jpg", IMAGE_JPEG_VALUE,
			"pt workout image".getBytes());

		// when
		var jsonRequest = new MockMultipartFile("request", "", APPLICATION_JSON_VALUE,
			requestJson.getBytes());

		var result = mockMvc.perform(multipart("/workout-records")
			.file(jsonRequest)
			.file(image)
			.contentType(MULTIPART_FORM_DATA_VALUE));

		// then
		result.andExpect(status().isCreated())
			.andDo(print());
	}

	@Test
	@DisplayName("통합 테스트 - 다양한 운동 루틴 운동 기록 등록 성공")
	void create_workout_record_multiple_routines_success() throws Exception {
		// given
		Member member = MemberFixture.getTrainerMember1();
		member = memberRepository.save(member);

		setAuthentication(member);

		String requestJson = """
			{
				"ptLessonId": 300,
				"date": "2025-01-21T09:00:00",
				"recordType": "PT",
				"routines": [
					{
						"workoutId": 10,
						"sets": [
							{"durationMinutes": null, "repetition": 12, "weight": 40},
							{"durationMinutes": null, "repetition": 10, "weight": 40},
							{"durationMinutes": null, "repetition": 8, "weight": 45}
						]
					},
					{
						"workoutId": 11,
						"sets": [
							{"durationMinutes": null, "repetition": 15, "weight": 30},
							{"durationMinutes": null, "repetition": 15, "weight": 30}
						]
					},
					{
						"workoutId": 12,
						"sets": [
							{"durationMinutes": 20, "repetition": null, "weight": null}
						]
					}
				],
				"feedback": "다양한 운동을 진행했습니다."
			}
			""";

		MockMultipartFile image1 = new MockMultipartFile("images", "routine1.jpg", IMAGE_JPEG_VALUE,
			"routine 1".getBytes());
		MockMultipartFile image2 = new MockMultipartFile("images", "routine2.jpg", IMAGE_JPEG_VALUE,
			"routine 2".getBytes());
		MockMultipartFile image3 = new MockMultipartFile("images", "routine3.jpg", IMAGE_JPEG_VALUE,
			"routine 3".getBytes());

		// when
		var jsonRequest = new MockMultipartFile("request", "", APPLICATION_JSON_VALUE,
			requestJson.getBytes());

		var result = mockMvc.perform(multipart("/workout-records")
			.file(jsonRequest)
			.file(image1)
			.file(image2)
			.file(image3)
			.contentType(MULTIPART_FORM_DATA_VALUE));

		// then
		result.andExpect(status().isCreated())
			.andDo(print());
	}

	@Test
	@DisplayName("통합 테스트 - 운동 기록 조회 성공")
	void get_workout_record_success() throws Exception {
		// given
		Member member = MemberFixture.getTraineeMember1();
		member = memberRepository.save(member);

		setAuthentication(member);

		// 운동 기록 직접 생성 및 저장
		WorkoutRecord workoutRecord = WorkoutRecord.builder()
			.memberId(member.getId())
			.ptLessonId(null)
			.date(LocalDateTime.of(2025, 1, 18, 15, 0))
			.recordType(TRAINEE)
			.workoutNote(WorkoutNote.builder().feedback("조회 테스트용 기록").build())
			.build();

		workoutRecord = workoutRecordRepository.save(workoutRecord);

		Routine routine = Routine.builder()
			.workoutRecordId(workoutRecord.getId())
			.workoutId(5L)
			.build();

		List<Routine> savedRoutines = routineRepository.saveAll(List.of(routine));
		routine = savedRoutines.getFirst();

		Set set = Set.builder()
			.routineId(routine.getId())
			.durationMinutes(null)
			.repetition(10)
			.weight(55)
			.build();

		setRepository.saveAll(List.of(set));

		// when & then
		mockMvc.perform(get("/workout-records/{recordId}", workoutRecord.getId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(workoutRecord.getId()))
			.andExpect(jsonPath("$.date").value("2025-01-18T15:00:00"))
			.andExpect(jsonPath("$.recordType").value("TRAINEE"))
			.andExpect(jsonPath("$.feedback").value("조회 테스트용 기록"))
			.andExpect(jsonPath("$.routines").isArray())
			.andDo(print());
	}

	@Test
	@DisplayName("통합 테스트 - 운동 기록 수정 성공")
	void update_workout_record_success() throws Exception {
		// given
		Member member = MemberFixture.getTraineeMember1();
		member = memberRepository.save(member);

		setAuthentication(member);

		// 운동 기록 직접 생성 및 저장
		WorkoutRecord workoutRecord = WorkoutRecord.builder()
			.memberId(member.getId())
			.ptLessonId(null)
			.date(LocalDateTime.of(2025, 1, 19, 16, 0))
			.recordType(TRAINEE)
			.workoutNote(WorkoutNote.builder().feedback("수정 전 피드백").build())
			.build();

		workoutRecord = workoutRecordRepository.save(workoutRecord);

		Routine routine = Routine.builder()
			.workoutRecordId(workoutRecord.getId())
			.workoutId(6L)
			.build();

		List<Routine> savedRoutines = routineRepository.saveAll(List.of(routine));
		routine = savedRoutines.getFirst();

		Set set = Set.builder()
			.routineId(routine.getId())
			.durationMinutes(null)
			.repetition(12)
			.weight(60)
			.build();

		setRepository.saveAll(List.of(set));

		// 수정 요청 데이터
		String updateRequest = """
			{
				"date": "2025-01-19T17:00:00",
				"routines": [
					{
						"workoutId": 6,
						"sets": [
							{"durationMinutes": null, "repetition": 15, "weight": 65}
						]
					},
					{
						"workoutId": 7,
						"sets": [
							{"durationMinutes": 25, "repetition": null, "weight": null}
						]
					}
				],
				"feedback": "수정된 피드백",
				"imageUrlsToDelete": []
			}
			""";

		var updateJsonRequest = new MockMultipartFile("request", "", APPLICATION_JSON_VALUE,
			updateRequest.getBytes());

		// when & then
		mockMvc.perform(multipart(PUT, "/workout-records/{recordId}", workoutRecord.getId())
				.file(updateJsonRequest)
				.contentType(MULTIPART_FORM_DATA_VALUE))
			.andExpect(status().isNoContent())
			.andDo(print());
	}

	@Test
	@DisplayName("통합 테스트 - 운동 기록 삭제 성공")
	void delete_workout_record_success() throws Exception {
		// given
		Member member = MemberFixture.getTraineeMember1();
		member = memberRepository.save(member);

		setAuthentication(member);

		// 운동 기록 직접 생성 및 저장
		WorkoutRecord workoutRecord = WorkoutRecord.builder()
			.memberId(member.getId())
			.ptLessonId(null)
			.date(LocalDateTime.of(2025, 1, 20, 18, 0))
			.recordType(TRAINEE)
			.workoutNote(WorkoutNote.builder().feedback("삭제 될 기록").build())
			.build();

		workoutRecord = workoutRecordRepository.save(workoutRecord);

		Routine routine = Routine.builder()
			.workoutRecordId(workoutRecord.getId())
			.workoutId(8L)
			.build();

		List<Routine> savedRoutines = routineRepository.saveAll(List.of(routine));
		routine = savedRoutines.getFirst();

		Set set = Set.builder()
			.routineId(routine.getId())
			.durationMinutes(null)
			.repetition(10)
			.weight(50)
			.build();

		setRepository.saveAll(List.of(set));

		// when & then
		mockMvc.perform(delete("/workout-records/{recordId}", workoutRecord.getId()))
			.andExpect(status().isNoContent())
			.andDo(print());
	}

	private void setAuthentication(Member member) {
		CustomUserDetails userDetails = new CustomUserDetails(member.getId(),
			member.getId().toString(),
			authoritiesMapper.mapAuthorities(List.of(new SimpleGrantedAuthority("ROLE_USER"))));

		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
			authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
