package com.tnt.pt.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PtLessonJpaRepository extends JpaRepository<PtLessonJpaEntity, Long> {

	List<PtLessonJpaEntity> findAllByPtTrainerTraineeAndDeletedAtIsNull(
		PtTrainerTraineeJpaEntity ptTrainerTraineeJpaEntity);

	List<PtLessonJpaEntity> findAllByPtTrainerTraineeAndIsCompletedIsFalseAndDeletedAtIsNull(
		PtTrainerTraineeJpaEntity ptTrainerTraineeJpaEntity);
}
