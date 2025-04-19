package com.tnt.member.dto;

import com.tnt.member.domain.SocialType;

public record WithdrawDto(
	String socialId,
	SocialType socialType,
	String profileImageUrl
) {

}
