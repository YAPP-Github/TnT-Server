package com.tnt.application.trainer;

import static com.tnt.common.error.model.ErrorMessage.TRAINER_NOT_FOUND;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tnt.common.error.exception.NotFoundException;
import com.tnt.domain.trainer.Trainer;
import com.tnt.dto.trainer.response.InvitationCodeResponse;
import com.tnt.dto.trainer.response.InvitationCodeVerifyResponse;
import com.tnt.infrastructure.mysql.repository.trainer.TrainerRepository;
import com.tnt.infrastructure.mysql.repository.trainer.TrainerSearchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainerService {

	private final TrainerRepository trainerRepository;
	private final TrainerSearchRepository trainerSearchRepository;

	@Transactional
	public InvitationCodeResponse reissueInvitationCode(Long memberId) {
		Trainer trainer = getByMemberId(memberId);
		trainer.setNewInvitationCode();

		return new InvitationCodeResponse(trainer.getInvitationCode());
	}

	public InvitationCodeResponse getInvitationCode(Long memberId) {
		Trainer trainer = getByMemberId(memberId);

		return new InvitationCodeResponse(trainer.getInvitationCode());
	}

	public InvitationCodeVerifyResponse verifyInvitationCode(String invitationCode) {
		boolean isVerified = trainerRepository.existsByInvitationCodeAndDeletedAtIsNull(invitationCode);
		String trainerName = isVerified ? getByInvitationCode(invitationCode).getMember().getName() : null;

		return new InvitationCodeVerifyResponse(isVerified, trainerName);
	}

	public Trainer getByMemberId(Long memberId) {
		return trainerRepository.findByMemberIdAndDeletedAtIsNull(memberId)
			.orElseThrow(() -> new NotFoundException(TRAINER_NOT_FOUND));
	}

	public Trainer getByInvitationCode(String invitationCode) {
		return trainerSearchRepository.find(null, invitationCode)
			.orElseThrow(() -> new NotFoundException(TRAINER_NOT_FOUND));
	}

	public void validateTrainerRegistration(Long memberId) {
		if (!trainerRepository.existsByMemberIdAndDeletedAtIsNull(memberId)) {
			throw new NotFoundException(TRAINER_NOT_FOUND);
		}
	}
}
