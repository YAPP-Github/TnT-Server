package com.tnt.pt.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tnt.pt.domain.PtLesson;
import com.tnt.pt.domain.PtTrainerTrainee;

public interface PtLessonRepository extends JpaRepository<PtLesson, Long> {

	List<PtLesson> findAllByPtTrainerTraineeAndDeletedAtIsNull(PtTrainerTrainee ptTrainerTrainee);

	List<PtLesson> findAllByPtTrainerTraineeAndIsCompletedIsFalseAndDeletedAtIsNull(PtTrainerTrainee ptTrainerTrainee);
}
