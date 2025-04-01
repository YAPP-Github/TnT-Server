package com.tnt.trainee.application;

import static com.tnt.common.error.model.ErrorMessage.TRAINEE_NOT_FOUND;

import org.springframework.stereotype.Service;

import com.tnt.common.error.exception.NotFoundException;
import com.tnt.trainee.domain.Trainee;
import com.tnt.trainee.infrastructure.TraineeRepository;
import com.tnt.trainee.infrastructure.TraineeSearchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TraineeService {

	private final TraineeRepository traineeRepository;
	private final TraineeSearchRepository traineeSearchRepository;

	public Trainee getByMemberId(Long memberId) {
		return traineeSearchRepository.find(memberId, null)
			.orElseThrow(() -> new NotFoundException(TRAINEE_NOT_FOUND));
	}

	public Trainee getByMemberIdNoFetch(Long memberId) {
		return traineeRepository.findByMemberIdAndDeletedAtIsNull(memberId)
			.orElseThrow(() -> new NotFoundException(TRAINEE_NOT_FOUND));
	}

	public Trainee getByTraineeId(Long traineeId) {
		return traineeSearchRepository.find(null, traineeId)
			.orElseThrow(() -> new NotFoundException(TRAINEE_NOT_FOUND));
	}
}
