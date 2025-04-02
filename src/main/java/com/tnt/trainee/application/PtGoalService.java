package com.tnt.trainee.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tnt.trainee.application.repository.PtGoalRepository;
import com.tnt.trainee.domain.PtGoal;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PtGoalService {

	private final PtGoalRepository ptGoalRepository;

	public List<PtGoal> getAllByTraineeId(Long traineeId) {
		return ptGoalRepository.findAllByTraineeId(traineeId);
	}
}
