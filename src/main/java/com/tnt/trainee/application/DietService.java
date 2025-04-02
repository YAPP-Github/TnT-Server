package com.tnt.trainee.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tnt.trainee.application.repository.DietRepository;
import com.tnt.trainee.domain.Diet;
import com.tnt.trainee.domain.Trainee;
import com.tnt.trainee.dto.request.CreateDietRequest;
import com.tnt.trainee.dto.response.CreateDietResponse;
import com.tnt.trainee.dto.response.GetDietResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DietService {

	private final TraineeService traineeService;

	private final DietRepository dietRepository;

	@Transactional
	public CreateDietResponse addDiet(Long traineeId, CreateDietRequest request, String dietImageUrl) {
		Diet diet = Diet.builder()
			.traineeId(traineeId)
			.date(request.date())
			.dietImageUrl(dietImageUrl)
			.memo(request.memo())
			.dietType(request.dietType())
			.build();

		Diet saveDiet = dietRepository.save(diet);

		return new CreateDietResponse(saveDiet.getId(), saveDiet.getDate(), saveDiet.getDietImageUrl(),
			saveDiet.getDietType(), saveDiet.getMemo());
	}

	@Transactional(readOnly = true)
	public GetDietResponse getDiet(Long memberId, Long dietId) {
		Trainee trainee = traineeService.getByMemberId(memberId);

		Diet diet = dietRepository.findByIdAndTraineeId(dietId, trainee.getId());

		return new GetDietResponse(diet.getId(), diet.getDate(), diet.getDietImageUrl(), diet.getDietType(),
			diet.getMemo());
	}

	public List<Diet> getAllByTraineeId(Long traineeId) {
		return dietRepository.findAllByTraineeId(traineeId);
	}

	public List<Diet> getAllByTraineeIdForDaily(Long traineeId, LocalDate date) {
		return dietRepository.findAllByTraineeIdForDaily(traineeId, date);
	}

	public List<Diet> getAllByTraineeIdForTraineeCalendar(Long traineeId, LocalDate startDate, LocalDate endDate) {
		return dietRepository.findAllByTraineeIdForTraineeCalendar(traineeId, startDate, endDate);
	}

	public boolean isDietExistByTraineeIdAndDate(Long traineeId, LocalDateTime date) {
		return dietRepository.existsByTraineeIdAndDate(traineeId, date);
	}
}
