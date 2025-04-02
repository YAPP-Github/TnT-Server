package com.tnt.trainee.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tnt.trainee.domain.Trainee;

public interface TraineeJpaRepository extends JpaRepository<Trainee, Long> {

	Optional<Trainee> findByMemberIdAndDeletedAtIsNull(Long memberId);
}
