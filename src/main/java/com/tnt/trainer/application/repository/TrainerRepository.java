package com.tnt.trainer.application.repository;

import com.tnt.trainer.domain.Trainer;

public interface TrainerRepository {

	Trainer save(Trainer trainer);

	Trainer findByMemberId(Long memberId);

	boolean existsByMemberId(Long memberId);

	boolean existsByInvitationCode(String invitationCode);

	Trainer find(Long memberId, String invitationCode);
}
