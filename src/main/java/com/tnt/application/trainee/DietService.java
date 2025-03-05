package com.tnt.application.trainee;

import static com.tnt.common.error.model.ErrorMessage.DIET_NOT_FOUND;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tnt.common.error.exception.NotFoundException;
import com.tnt.domain.trainee.Diet;
import com.tnt.domain.trainee.Trainee;
import com.tnt.dto.trainee.request.CreateDietRequest;
import com.tnt.dto.trainee.response.CreateDietResponse;
import com.tnt.dto.trainee.response.GetDietResponse;
import com.tnt.infrastructure.mysql.repository.trainee.DietRepository;
import com.tnt.infrastructure.mysql.repository.trainee.DietSearchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DietService {

	private final TraineeService traineeService;

	private final DietRepository dietRepository;
	private final DietSearchRepository dietSearchRepository;

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

		Diet diet = getByDietIdAndTraineeId(dietId, trainee.getId());

		return new GetDietResponse(diet.getId(), diet.getDate(), diet.getDietImageUrl(), diet.getDietType(),
			diet.getMemo());
	}

	public Diet getByDietIdAndTraineeId(Long dietId, Long traineeId) {
		return dietRepository.findByIdAndTraineeIdAndDeletedAtIsNull(dietId, traineeId)
			.orElseThrow(() -> new NotFoundException(DIET_NOT_FOUND));
	}

	public List<Diet> getAllByTraineeId(Long traineeId) {
		return dietRepository.findAllByTraineeIdAndDeletedAtIsNull(traineeId);
	}

	public List<Diet> getAllByTraineeIdForDaily(Long traineeId, LocalDate date) {
		return dietSearchRepository.findAllByTraineeIdForDaily(traineeId, date);
	}

	public List<Diet> getAllByTraineeIdForTraineeCalendar(Long traineeId, LocalDate startDate, LocalDate endDate) {
		return dietSearchRepository.findAllByTraineeIdForTraineeCalendar(traineeId, startDate, endDate);
	}

	public boolean isDietExistByTraineeIdAndDate(Long traineeId, LocalDateTime date) {
		return dietRepository.existsByTraineeIdAndDateAndDeletedAtIsNull(traineeId, date);
	}
}
