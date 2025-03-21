package com.tnt.application.trainee;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tnt.domain.trainee.PtGoal;
import com.tnt.infrastructure.mysql.repository.pt.PtGoalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PtGoalService {

	private final PtGoalRepository ptGoalRepository;

	public List<PtGoal> getAllByTraineeId(Long traineeId) {
		return ptGoalRepository.findAllByTraineeId(traineeId);
	}
}
