package com.tnt.member.application.repository;

import com.tnt.member.domain.Member;
import com.tnt.member.domain.SocialType;
import com.tnt.member.dto.MemberProjection;

public interface MemberRepository {

	Member save(Member member);

	Member findBySocialIdAndSocialType(String socialId, SocialType socialType);

	boolean existsBySocialIdAndSocialType(String socialId, SocialType socialType);

	Member findById(Long memberId);

	MemberProjection.MemberTypeDto findMemberType(Long memberId);
}
