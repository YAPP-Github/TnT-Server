package com.tnt.member.infrastructure;

import static com.tnt.common.error.model.ErrorMessage.MEMBER_NOT_FOUND;
import static com.tnt.member.domain.QMember.member;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tnt.common.error.exception.NotFoundException;
import com.tnt.member.application.repository.MemberRepository;
import com.tnt.member.domain.Member;
import com.tnt.member.domain.SocialType;
import com.tnt.member.dto.MemberProjection;
import com.tnt.member.dto.QMemberProjection_MemberTypeDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

	private final MemberJpaRepository memberJpaRepository;
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Member save(Member member) {
		return memberJpaRepository.save(member);
	}

	@Override
	public Member findBySocialIdAndSocialTypeAndDeletedAtIsNull(String socialId, SocialType socialType) {
		return memberJpaRepository.findBySocialIdAndSocialTypeAndDeletedAtIsNull(socialId, socialType)
			.orElseThrow(() -> new NotFoundException(MEMBER_NOT_FOUND));
	}

	@Override
	public boolean existsBySocialIdAndSocialTypeAndDeletedAtIsNull(String socialId, SocialType socialType) {
		return memberJpaRepository.existsBySocialIdAndSocialTypeAndDeletedAtIsNull(socialId, socialType);
	}

	@Override
	public Member findByIdAndDeletedAtIsNull(Long memberId) {
		return memberJpaRepository.findByIdAndDeletedAtIsNull(memberId)
			.orElseThrow(() -> new NotFoundException(MEMBER_NOT_FOUND));
	}

	@Override
	public MemberProjection.MemberTypeDto findMemberType(Long memberId) {
		return Optional.ofNullable(jpaQueryFactory
				.select(new QMemberProjection_MemberTypeDto(member.memberType))
				.from(member)
				.where(
					member.id.eq(memberId),
					member.deletedAt.isNull()
				)
				.fetchOne())
			.orElseThrow(() -> new NotFoundException(MEMBER_NOT_FOUND));
	}
}
