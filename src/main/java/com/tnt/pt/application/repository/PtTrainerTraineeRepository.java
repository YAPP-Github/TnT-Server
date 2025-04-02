package com.tnt.pt.application.repository;

import java.util.List;

import com.tnt.pt.domain.PtTrainerTrainee;
import com.tnt.trainee.domain.Trainee;

public interface PtTrainerTraineeRepository {

	PtTrainerTrainee save(PtTrainerTrainee ptTrainerTrainee);

	void saveAll(List<PtTrainerTrainee> ptTrainerTrainees);

	PtTrainerTrainee findByTrainerId(Long trainerId);

	PtTrainerTrainee findByTraineeId(Long traineeId);

	List<PtTrainerTrainee> findAllByTrainerId(Long trainerId);

	List<PtTrainerTrainee> findAllByTrainerIdWithDeleted(Long trainerId);

	boolean existsByTrainerId(Long trainerId);

	boolean existsByTraineeId(Long traineeId);

	boolean existsByTrainerIdAndTraineeId(Long trainerId, Long traineeId);

	List<Trainee> findAllTrainees(Long trainerId);
}
