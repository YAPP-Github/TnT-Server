package com.tnt.pt.application.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.tnt.pt.domain.PtLesson;
import com.tnt.pt.domain.PtTrainerTrainee;
import com.tnt.pt.dto.PtTrainerTraineeProjection;

public interface PtLessonRepository {

	PtLesson save(PtLesson ptLesson);

	void saveAll(List<PtLesson> ptLessons);

	List<PtLesson> findAll();

	List<PtLesson> findAllByPtTrainerTrainee(PtTrainerTrainee ptTrainerTrainee);

	List<PtLesson> findAllByPtTrainerTraineeAndIsCompletedIsFalse(PtTrainerTrainee ptTrainerTrainee);

	List<PtLesson> findAllByTrainerIdAndDate(Long trainerId, LocalDate date);

	List<PtLesson> findAllByTraineeIdForTrainerCalendar(Long traineeId, Integer year, Integer month);

	List<PtLesson> findAllByTraineeIdForTraineeCalendar(Long traineeId, LocalDate startDate, LocalDate endDate);

	Optional<PtTrainerTraineeProjection.PtInfoDto> findPtInfoByTraineeIdForDaily(Long traineeId, LocalDate date);

	PtLesson findById(Long id);

	boolean existsByStartAndEnd(PtTrainerTrainee pt, LocalDateTime start, LocalDateTime end);

	boolean existsByStart(PtTrainerTrainee pt, LocalDateTime start);
}
