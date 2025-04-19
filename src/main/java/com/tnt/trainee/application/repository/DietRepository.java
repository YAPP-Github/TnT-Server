package com.tnt.trainee.application.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.tnt.trainee.domain.Diet;

public interface DietRepository {

	Diet save(Diet diet);

	void saveAll(List<Diet> diets);

	Diet findByIdAndTraineeId(Long id, Long traineeId);

	List<Diet> findAllByTraineeId(Long traineeId);

	List<Diet> findAllByTraineeIdForDaily(Long traineeId, LocalDate date);

	List<Diet> findAllByTraineeIdForTraineeCalendar(Long traineeId, LocalDate startDate, LocalDate endDate);

	boolean existsByTraineeIdAndDate(Long traineeId, LocalDateTime date);
}
