package com.tnt.trainee.application;

import org.springframework.stereotype.Service;

import com.tnt.trainee.application.repository.TraineeRepository;
import com.tnt.trainee.domain.Trainee;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TraineeService {

	private final TraineeRepository traineeRepository;

	public Trainee getByMemberId(Long memberId) {
		return traineeRepository.find(memberId, null);
	}

	public Trainee getByMemberIdNoFetch(Long memberId) {
		return traineeRepository.findByMemberId(memberId);
	}

	public Trainee getByTraineeId(Long traineeId) {
		return traineeRepository.find(null, traineeId);
	}
}
