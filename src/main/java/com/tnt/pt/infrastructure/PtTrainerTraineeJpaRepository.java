package com.tnt.pt.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tnt.pt.domain.PtTrainerTrainee;

public interface PtTrainerTraineeJpaRepository extends JpaRepository<PtTrainerTrainee, Long> {

	Optional<PtTrainerTrainee> findByTrainerIdAndDeletedAtIsNull(Long trainerId);

	Optional<PtTrainerTrainee> findByTraineeIdAndDeletedAtIsNull(Long traineeId);

	List<PtTrainerTrainee> findAllByTrainerId(Long trainerId);

	List<PtTrainerTrainee> findAllByTrainerIdAndDeletedAtIsNull(Long trainerId);

	boolean existsByTrainerIdAndDeletedAtIsNull(Long trainerId);

	boolean existsByTraineeIdAndDeletedAtIsNull(Long traineeId);

	boolean existsByTrainerIdAndTraineeIdAndDeletedAtIsNull(Long trainerId, Long traineeId);
}
