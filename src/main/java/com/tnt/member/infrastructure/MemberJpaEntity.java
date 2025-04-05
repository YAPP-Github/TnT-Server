package com.tnt.member.infrastructure;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.tnt.common.jpa.BaseTimeEntity;
import com.tnt.member.domain.Member;
import com.tnt.member.domain.MemberType;
import com.tnt.member.domain.SocialType;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberJpaEntity extends BaseTimeEntity {

	@Id
	@Tsid
	@Column(name = "id", nullable = false, unique = true)
	private Long id;

	@Column(name = "social_id", nullable = true, unique = true)
	private String socialId;

	@Column(name = "fcm_token", nullable = false, length = 255)
	private String fcmToken;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "profile_image_url", nullable = false)
	private String profileImageUrl;

	@Column(name = "birthday", nullable = true)
	private LocalDate birthday;

	@Column(name = "service_agreement", nullable = false)
	private Boolean serviceAgreement;

	@Column(name = "collection_agreement", nullable = false)
	private Boolean collectionAgreement;

	@Column(name = "advertisement_agreement", nullable = false)
	private Boolean advertisementAgreement;

	@Column(name = "deleted_at", nullable = true)
	private LocalDateTime deletedAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "social_type", nullable = false)
	private SocialType socialType;

	@Enumerated(EnumType.STRING)
	@Column(name = "member_type", nullable = false)
	private MemberType memberType;

	@Builder
	public MemberJpaEntity(Long id, String socialId, String fcmToken, String email, String name, String profileImageUrl,
		LocalDate birthday, Boolean serviceAgreement, Boolean collectionAgreement, Boolean advertisementAgreement,
		SocialType socialType, MemberType memberType, LocalDateTime deletedAt) {

		this.id = id;
		this.socialId = socialId;
		this.fcmToken = fcmToken;
		this.email = email;
		this.name = name;
		this.profileImageUrl = profileImageUrl;
		this.birthday = birthday;
		this.serviceAgreement = serviceAgreement;
		this.collectionAgreement = collectionAgreement;
		this.advertisementAgreement = advertisementAgreement;
		this.socialType = socialType;
		this.memberType = memberType;
		this.deletedAt = deletedAt;
	}

	public static MemberJpaEntity from(Member member) {
		return MemberJpaEntity.builder()
			.id(member.getId())
			.socialId(member.getSocialId())
			.fcmToken(member.getFcmToken())
			.email(member.getEmail())
			.name(member.getName())
			.profileImageUrl(member.getProfileImageUrl())
			.birthday(member.getBirthday())
			.serviceAgreement(member.getServiceAgreement())
			.collectionAgreement(member.getCollectionAgreement())
			.advertisementAgreement(member.getAdvertisementAgreement())
			.socialType(member.getSocialType())
			.memberType(member.getMemberType())
			.deletedAt(member.getDeletedAt())
			.build();
	}

	public Member toModel() {
		return Member.builder()
			.id(this.id)
			.socialId(this.socialId)
			.fcmToken(this.fcmToken)
			.email(this.email)
			.name(this.name)
			.profileImageUrl(this.profileImageUrl)
			.birthday(this.birthday)
			.serviceAgreement(this.serviceAgreement)
			.collectionAgreement(this.collectionAgreement)
			.advertisementAgreement(this.advertisementAgreement)
			.socialType(this.socialType)
			.memberType(this.memberType)
			.deletedAt(this.deletedAt)
			.build();
	}
}
