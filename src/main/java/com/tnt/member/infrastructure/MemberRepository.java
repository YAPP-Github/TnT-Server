package com.tnt.member.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tnt.member.domain.Member;
import com.tnt.member.domain.SocialType;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findBySocialIdAndSocialTypeAndDeletedAtIsNull(String socialId, SocialType socialType);

	Optional<Member> findByIdAndDeletedAtIsNull(Long memberId);
}
