package com.tnt.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.tnt.member.domain.MemberType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberProjection {

	@QueryProjection
	public record MemberTypeDto(MemberType memberType) {

	}
}
