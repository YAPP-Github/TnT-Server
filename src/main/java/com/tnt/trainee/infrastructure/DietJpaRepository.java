package com.tnt.trainee.infrastructure;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DietJpaRepository extends JpaRepository<DietJpaEntity, Long> {

	Optional<DietJpaEntity> findByIdAndTraineeIdAndDeletedAtIsNull(Long id, Long traineeId);

	List<DietJpaEntity> findAllByTraineeIdAndDeletedAtIsNull(Long traineeId);

	boolean existsByTraineeIdAndDateAndDeletedAtIsNull(Long traineeId, LocalDateTime date);
}
