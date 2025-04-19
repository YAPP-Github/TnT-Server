package com.tnt.member.infrastructure;

import static com.tnt.common.error.model.ErrorMessage.MEMBER_NOT_FOUND;
import static com.tnt.member.infrastructure.QMemberJpaEntity.memberJpaEntity;

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
		return memberJpaRepository.save(MemberJpaEntity.from(member)).toModel();
	}

	@Override
	public Member findBySocialIdAndSocialType(String socialId, SocialType socialType) {
		return memberJpaRepository.findBySocialIdAndSocialTypeAndDeletedAtIsNull(socialId, socialType)
			.orElseThrow(() -> new NotFoundException(MEMBER_NOT_FOUND)).toModel();
	}

	@Override
	public boolean existsBySocialIdAndSocialType(String socialId, SocialType socialType) {
		return memberJpaRepository.existsBySocialIdAndSocialTypeAndDeletedAtIsNull(socialId, socialType);
	}

	@Override
	public Member findById(Long memberId) {
		return memberJpaRepository.findByIdAndDeletedAtIsNull(memberId)
			.orElseThrow(() -> new NotFoundException(MEMBER_NOT_FOUND)).toModel();
	}

	@Override
	public MemberProjection.MemberTypeDto findMemberType(Long memberId) {
		return Optional.ofNullable(jpaQueryFactory
				.select(new QMemberProjection_MemberTypeDto(memberJpaEntity.memberType))
				.from(memberJpaEntity)
				.where(
					memberJpaEntity.id.eq(memberId),
					memberJpaEntity.deletedAt.isNull()
				)
				.fetchOne())
			.orElseThrow(() -> new NotFoundException(MEMBER_NOT_FOUND));
	}
}
