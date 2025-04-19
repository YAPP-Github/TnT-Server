package com.tnt.trainee.application.repository;

import org.springframework.lang.Nullable;

import com.tnt.trainee.domain.Trainee;

public interface TraineeRepository {

	Trainee save(Trainee trainee);

	Trainee findByMemberId(Long memberId);

	Trainee find(@Nullable Long memberId, @Nullable Long traineeId);
}
