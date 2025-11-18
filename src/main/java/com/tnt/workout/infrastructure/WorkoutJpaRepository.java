package com.tnt.workout.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutJpaRepository extends JpaRepository<WorkoutJpaEntity, Long> {
}
