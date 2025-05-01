package com.tnt.trainee.application.repository;

import java.util.List;

import org.springframework.lang.Nullable;

import com.tnt.trainee.domain.Trainee;

public interface TraineeRepository {

	Trainee save(Trainee trainee);

	Trainee findByMemberId(Long memberId);

	Trainee find(@Nullable Long memberId, @Nullable Long traineeId);

	List<Trainee> findAll();
}
