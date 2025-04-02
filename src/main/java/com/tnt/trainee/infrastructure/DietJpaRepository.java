package com.tnt.trainee.infrastructure;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tnt.trainee.domain.Diet;

public interface DietJpaRepository extends JpaRepository<Diet, Long> {

	Optional<Diet> findByIdAndTraineeIdAndDeletedAtIsNull(Long id, Long traineeId);

	List<Diet> findAllByTraineeIdAndDeletedAtIsNull(Long traineeId);

	boolean existsByTraineeIdAndDateAndDeletedAtIsNull(Long traineeId, LocalDateTime date);
}
