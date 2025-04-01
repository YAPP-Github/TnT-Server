package com.tnt.member.infrastructure;

import static com.tnt.member.domain.QMember.member;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tnt.member.dto.MemberProjection;
import com.tnt.member.dto.QMemberProjection_MemberTypeDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberSearchRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Optional<MemberProjection.MemberTypeDto> findMemberType(Long memberId) {
		return Optional.ofNullable(jpaQueryFactory
			.select(new QMemberProjection_MemberTypeDto(member.memberType))
			.from(member)
			.where(
				member.id.eq(memberId),
				member.deletedAt.isNull()
			)
			.fetchOne());
	}
}
