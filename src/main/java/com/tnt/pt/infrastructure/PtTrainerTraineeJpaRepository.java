package com.tnt.pt.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PtTrainerTraineeJpaRepository extends JpaRepository<PtTrainerTraineeJpaEntity, Long> {

	Optional<PtTrainerTraineeJpaEntity> findByTrainerIdAndDeletedAtIsNull(Long trainerId);

	Optional<PtTrainerTraineeJpaEntity> findByTraineeIdAndDeletedAtIsNull(Long traineeId);

	List<PtTrainerTraineeJpaEntity> findAllByTrainerId(Long trainerId);

	List<PtTrainerTraineeJpaEntity> findAllByTrainerIdAndDeletedAtIsNull(Long trainerId);

	boolean existsByTrainerIdAndDeletedAtIsNull(Long trainerId);

	boolean existsByTraineeIdAndDeletedAtIsNull(Long traineeId);

	boolean existsByTrainerIdAndTraineeIdAndDeletedAtIsNull(Long trainerId, Long traineeId);
}
