package com.tnt.member.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tnt.member.domain.SocialType;

public interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, Long> {

	Optional<MemberJpaEntity> findBySocialIdAndSocialTypeAndDeletedAtIsNull(String socialId, SocialType socialType);

	boolean existsBySocialIdAndSocialTypeAndDeletedAtIsNull(String socialId, SocialType socialType);

	Optional<MemberJpaEntity> findByIdAndDeletedAtIsNull(Long memberId);
}
