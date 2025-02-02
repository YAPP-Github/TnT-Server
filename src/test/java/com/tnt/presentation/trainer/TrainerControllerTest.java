package com.tnt.presentation.trainer;

import static com.tnt.domain.member.MemberType.TRAINER;
import static com.tnt.domain.member.SocialType.KAKAO;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tnt.annotation.WithMockCustomUser;
import com.tnt.domain.member.Member;
import com.tnt.domain.pt.PtLesson;
import com.tnt.domain.pt.PtTrainerTrainee;
import com.tnt.domain.trainee.PtGoal;
import com.tnt.domain.trainee.Trainee;
import com.tnt.domain.trainer.Trainer;
import com.tnt.fixture.MemberFixture;
import com.tnt.fixture.PtTrainerTraineeFixture;
import com.tnt.fixture.TraineeFixture;
import com.tnt.fixture.TrainerFixture;
import com.tnt.gateway.filter.CustomUserDetails;
import com.tnt.infrastructure.mysql.repository.member.MemberRepository;
import com.tnt.infrastructure.mysql.repository.pt.PtGoalRepository;
import com.tnt.infrastructure.mysql.repository.pt.PtLessonRepository;
import com.tnt.infrastructure.mysql.repository.pt.PtTrainerTraineeRepository;
import com.tnt.infrastructure.mysql.repository.trainee.TraineeRepository;
import com.tnt.infrastructure.mysql.repository.trainer.TrainerRepository;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class TrainerControllerTest {

	private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TrainerRepository trainerRepository;

	@Autowired
	private TraineeRepository traineeRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private PtTrainerTraineeRepository ptTrainerTraineeRepository;

	@Autowired
	private PtGoalRepository ptGoalRepository;

	@Autowired
	private PtLessonRepository ptLessonRepository;

	@Test
	@DisplayName("통합 테스트 - 트레이너 초대 코드 불러오기 성공")
	@WithMockCustomUser(memberId = 1L)
	void get_invitation_code_success() throws Exception {
		// given
		Long memberId = 1L;
		String socialId = "1234567890";
		String email = "abc@gmail.com";
		String name = "김영명";
		String profileImageUrl = "https://profile.com/1234567890";

		Member member = Member.builder()
			.id(memberId)
			.socialId(socialId)
			.email(email)
			.name(name)
			.profileImageUrl(profileImageUrl)
			.serviceAgreement(true)
			.collectionAgreement(true)
			.advertisementAgreement(true)
			.socialType(KAKAO)
			.memberType(TRAINER)
			.build();

		Trainer trainer = Trainer.builder()
			.member(member)
			.build();

		trainerRepository.save(trainer);

		// when & then
		mockMvc.perform(get("/trainers/invitation-code")).andExpect(status().isOk());
	}

	@Test
	@DisplayName("통합 테스트 - 트레이너 초대 코드 불러오기 실패 - 존재하지 않는 계정")
	@WithMockCustomUser(memberId = 2L)
	void get_invitation_code_fail() throws Exception {
		// given
		Long memberId = 1L;
		String socialId = "1234567890";
		String email = "abc@gmail.com";
		String name = "김영명";
		String profileImageUrl = "https://profile.com/1234567890";

		Member member = Member.builder()
			.id(memberId)
			.socialId(socialId)
			.email(email)
			.name(name)
			.profileImageUrl(profileImageUrl)
			.serviceAgreement(true)
			.collectionAgreement(true)
			.advertisementAgreement(true)
			.socialType(KAKAO)
			.memberType(TRAINER)
			.build();

		Trainer trainer = Trainer.builder()
			.member(member)
			.build();

		trainerRepository.save(trainer);

		// when & then
		mockMvc.perform(get("/trainers/invitation-code")).andExpect(status().is4xxClientError());
	}

	@Test
	@DisplayName("통합 테스트 - 트레이너 초대 코드 재발급 성공")
	@WithMockCustomUser(memberId = 3L)
	void reissue_invitation_code_success() throws Exception {
		// given
		Long memberId = 3L;
		String socialId = "1234567890";
		String email = "abc@gmail.com";
		String name = "김영명";
		String profileImageUrl = "https://profile.com/1234567890";

		Member member = Member.builder()
			.id(memberId)
			.socialId(socialId)
			.email(email)
			.name(name)
			.profileImageUrl(profileImageUrl)
			.serviceAgreement(true)
			.collectionAgreement(true)
			.advertisementAgreement(true)
			.socialType(KAKAO)
			.memberType(TRAINER)
			.build();

		Trainer trainer = Trainer.builder()
			.member(member)
			.build();

		trainerRepository.save(trainer);

		// when & then
		mockMvc.perform(put("/trainers/invitation-code/reissue")).andExpect(status().isCreated());
	}

	@Test
	@DisplayName("통합 테스트 - 트레이너 초대 코드 인증 성공")
	void verify_invitation_code_success() throws Exception {
		// given
		Long memberId = 4L;
		String socialId = "1234567890";
		String email = "abc@gmail.com";
		String name = "김영명";
		String profileImageUrl = "https://profile.com/1234567890";

		Member member = Member.builder()
			.id(memberId)
			.socialId(socialId)
			.email(email)
			.name(name)
			.profileImageUrl(profileImageUrl)
			.serviceAgreement(true)
			.collectionAgreement(true)
			.advertisementAgreement(true)
			.socialType(KAKAO)
			.memberType(TRAINER)
			.build();

		Trainer trainer = Trainer.builder()
			.member(member)
			.build();

		trainerRepository.save(trainer);

		String invitationCode = trainer.getInvitationCode();

		// when & then
		mockMvc.perform(get("/trainers/invitation-code/verify/" + invitationCode))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isVerified").value(true));
	}

	@Test
	@DisplayName("통합 테스트 - 트레이너 초대 코드 인증 실패")
	void verify_invitation_code_fail() throws Exception {
		// given
		Long memberId = 5L;
		String socialId = "1234567890";
		String email = "abc@gmail.com";
		String name = "김영명";
		String profileImageUrl = "https://profile.com/1234567890";

		Member member = Member.builder()
			.id(memberId)
			.socialId(socialId)
			.email(email)
			.name(name)
			.profileImageUrl(profileImageUrl)
			.serviceAgreement(true)
			.collectionAgreement(true)
			.advertisementAgreement(true)
			.socialType(KAKAO)
			.memberType(TRAINER)
			.build();

		Trainer trainer = Trainer.builder()
			.member(member)
			.build();

		trainerRepository.save(trainer);

		String invitationCode = "noExistCode";

		// when & then
		mockMvc.perform(get("/trainers/invitation-code/verify/" + invitationCode))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isVerified").value(false));
	}

	@Test
	@DisplayName("통합 테스트 - 연결 완료된 트레이니 최초로 정보 가져오기 성공")
	void get_first_connected_trainee_success() throws Exception {
		// given
		Member trainerMember = MemberFixture.getTrainerMember1();
		Member traineeMember = MemberFixture.getTraineeMember1();

		trainerMember = memberRepository.save(trainerMember);
		traineeMember = memberRepository.save(traineeMember);

		CustomUserDetails trainerUserDetails = new CustomUserDetails(trainerMember.getId(),
			trainerMember.getId().toString(),
			authoritiesMapper.mapAuthorities(List.of(new SimpleGrantedAuthority("ROLE_USER"))));

		Authentication authentication = new UsernamePasswordAuthenticationToken(trainerUserDetails, null,
			authoritiesMapper.mapAuthorities(trainerUserDetails.getAuthorities()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		Trainer trainer = Trainer.builder()
			.member(trainerMember)
			.build();

		Trainee trainee = Trainee.builder()
			.member(traineeMember)
			.height(180.5)
			.weight(78.4)
			.cautionNote("주의사항")
			.build();

		trainer = trainerRepository.save(trainer);
		trainee = traineeRepository.save(trainee);

		PtTrainerTrainee ptTrainerTrainee = PtTrainerTraineeFixture.getPtTrainerTrainee1(trainer, trainee);

		ptTrainerTraineeRepository.save(ptTrainerTrainee);

		PtGoal ptGoal1 = PtGoal.builder()
			.traineeId(trainee.getId())
			.content("다이어트")
			.build();

		PtGoal ptGoal2 = PtGoal.builder()
			.traineeId(trainee.getId())
			.content("체중 감량")
			.build();

		ptGoalRepository.saveAll(List.of(ptGoal1, ptGoal2));

		// when & then
		mockMvc.perform(get("/trainers/first-connected-trainee")
				.param("trainerId", trainer.getId().toString())
				.param("traineeId", trainee.getId().toString()))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("통합 테스트 - 특정 날짜 PT 리스트 불러오기 성공")
	void get_pt_lessons_on_date_success() throws Exception {
		// given
		Member trainerMember = MemberFixture.getTrainerMember1();
		Member traineeMember = MemberFixture.getTraineeMember1();

		trainerMember = memberRepository.save(trainerMember);
		traineeMember = memberRepository.save(traineeMember);

		CustomUserDetails trainerUserDetails = new CustomUserDetails(trainerMember.getId(),
			trainerMember.getId().toString(),
			authoritiesMapper.mapAuthorities(List.of(new SimpleGrantedAuthority("ROLE_USER"))));

		Authentication authentication = new UsernamePasswordAuthenticationToken(trainerUserDetails, null,
			authoritiesMapper.mapAuthorities(trainerUserDetails.getAuthorities()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		Trainer trainer = TrainerFixture.getTrainer2(trainerMember);
		Trainee trainee = TraineeFixture.getTrainee2(traineeMember);
		trainer = trainerRepository.save(trainer);
		trainee = traineeRepository.save(trainee);

		PtTrainerTrainee ptTrainerTrainee = PtTrainerTraineeFixture.getPtTrainerTrainee1(trainer, trainee);
		ptTrainerTraineeRepository.save(ptTrainerTrainee);

		PtLesson ptLesson = PtLesson.builder()
			.ptTrainerTrainee(ptTrainerTrainee)
			.lessonStart(LocalDateTime.of(2025, 1, 1, 10, 0))
			.lessonEnd(LocalDateTime.of(2025, 1, 1, 11, 0))
			.memo("THIS IS MEMO")
			.build();

		ptLesson = ptLessonRepository.save(ptLesson);

		// when & then
		mockMvc.perform(get("/trainers/lessons/{date}", "2025-01-01"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.count").value(1))
			.andExpect(jsonPath("$.date").value("2025-01-01"))
			.andExpect(jsonPath("$.lessons[0].ptLessonId").value(ptLesson.getId()));
	}
}
