package com.tnt.trainer.application;

import static com.tnt.common.error.model.ErrorMessage.TRAINER_NOT_FOUND;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tnt.common.error.exception.NotFoundException;
import com.tnt.trainer.application.repository.TrainerRepository;
import com.tnt.trainer.domain.Trainer;
import com.tnt.trainer.dto.response.InvitationCodeResponse;
import com.tnt.trainer.dto.response.InvitationCodeVerifyResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainerService {

	private final TrainerRepository trainerRepository;

	@Transactional
	public InvitationCodeResponse reissueInvitationCode(Long memberId) {
		Trainer trainer = getByMemberId(memberId);
		trainer.setNewInvitationCode();

		trainerRepository.save(trainer);

		return new InvitationCodeResponse(trainer.getInvitationCode());
	}

	public InvitationCodeResponse getInvitationCode(Long memberId) {
		Trainer trainer = getByMemberId(memberId);

		return new InvitationCodeResponse(trainer.getInvitationCode());
	}

	public InvitationCodeVerifyResponse verifyInvitationCode(String invitationCode) {
		boolean isVerified = trainerRepository.existsByInvitationCode(invitationCode);
		String trainerName = isVerified ? getByInvitationCode(invitationCode).getMember().getName() : null;

		return new InvitationCodeVerifyResponse(isVerified, trainerName);
	}

	public Trainer getByMemberId(Long memberId) {
		return trainerRepository.findByMemberId(memberId);
	}

	public Trainer getByInvitationCode(String invitationCode) {
		return trainerRepository.find(null, invitationCode);
	}

	public void validateTrainerRegistration(Long memberId) {
		if (!trainerRepository.existsByMemberId(memberId)) {
			throw new NotFoundException(TRAINER_NOT_FOUND);
		}
	}
}
