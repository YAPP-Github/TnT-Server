package com.tnt.workoutrecord.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRecordJpaRepository extends JpaRepository<WorkoutRecordJpaEntity, Long> {

}
