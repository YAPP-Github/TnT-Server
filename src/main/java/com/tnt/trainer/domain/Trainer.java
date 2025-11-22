package com.tnt.trainer.domain;

import static com.tnt.common.error.model.ErrorMessage.TRAINER_INVALID_INVITATION_CODE;
import static com.tnt.common.error.model.ErrorMessage.TRAINER_INVITATION_CODE_GENERATE_FAILED;
import static java.util.Objects.requireNonNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.UUID;

import com.tnt.common.error.exception.TnTException;
import com.tnt.member.domain.Member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Trainer {

	public static final int INVITATION_CODE_LENGTH = 8;

	private final Long id;
	private final Member member;
	private String invitationCode;
	private LocalDateTime deletedAt;

	@Builder
	public Trainer(Long id, Member member, String invitationCode, LocalDateTime deletedAt) {
		this.id = id;
		this.member = requireNonNull(member);
		this.invitationCode = invitationCode;
		this.deletedAt = deletedAt;

		if (invitationCode == null) {
			setNewInvitationCode();
		}
	}

	public void setNewInvitationCode() {
		byte[] hashBytes;
		StringBuilder sb = new StringBuilder();

		String uuidString = UUID.randomUUID().toString();
		byte[] uuidStringBytes = uuidString.getBytes(StandardCharsets.UTF_8);

		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			hashBytes = messageDigest.digest(uuidStringBytes);
		} catch (NoSuchAlgorithmException e) {
			throw new TnTException(TRAINER_INVITATION_CODE_GENERATE_FAILED, e);
		}

		for (int j = 0; j < 4; j++) {
			sb.append(String.format("%02x", hashBytes[j]));
		}

		this.invitationCode = validateInvitationCode(sb.toString().toUpperCase());
	}

	private String validateInvitationCode(String invitationCode) {
		if (invitationCode == null || invitationCode.length() != INVITATION_CODE_LENGTH) {
			throw new IllegalArgumentException(TRAINER_INVALID_INVITATION_CODE.getMessage());
		}

		return invitationCode;
	}

	public void softDelete() {
		this.deletedAt = LocalDateTime.now();
	}
}
