package com.tnt.member.domain;

import static com.tnt.common.error.model.ErrorMessage.MEMBER_INVALID_COLLECTION_AGREEMENT;
import static com.tnt.common.error.model.ErrorMessage.MEMBER_INVALID_EMAIL;
import static com.tnt.common.error.model.ErrorMessage.MEMBER_INVALID_NAME;
import static com.tnt.common.error.model.ErrorMessage.MEMBER_INVALID_PROFILE_IMAGE_URL;
import static com.tnt.common.error.model.ErrorMessage.MEMBER_INVALID_SERVICE_AGREEMENT;
import static com.tnt.common.error.model.ErrorMessage.MEMBER_INVALID_SOCIAL_ID;
import static com.tnt.common.error.model.ErrorMessage.MEMBER_NULL_ADVERTISEMENT_AGREEMENT;
import static io.micrometer.common.util.StringUtils.isBlank;
import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Member {

	private static final int SOCIAL_ID_LENGTH = 50;
	private static final int EMAIL_LENGTH = 100;
	private static final int NAME_LENGTH = 50;
	private static final int PROFILE_IMAGE_URL_LENGTH = 255;

	private final Long id;
	private final String email;
	private final String name;
	private final LocalDate birthday;
	private final Boolean serviceAgreement;
	private final Boolean collectionAgreement;
	private final Boolean advertisementAgreement;
	private final SocialType socialType;
	private final MemberType memberType;
	private String socialId;
	private String fcmToken;
	private String profileImageUrl;
	private LocalDateTime deletedAt;

	@Builder
	public Member(Long id, String socialId, String fcmToken, String email, String name, String profileImageUrl,
		LocalDate birthday, Boolean serviceAgreement, Boolean collectionAgreement, Boolean advertisementAgreement,
		SocialType socialType, MemberType memberType, LocalDateTime deletedAt) {
		validateRequiredAgreements(serviceAgreement, collectionAgreement);

		this.id = id;
		this.socialId = validateSocialId(socialId);
		this.fcmToken = fcmToken;
		this.email = validateEmail(email);
		this.name = validateName(name);
		this.profileImageUrl = validateProfileImageUrl(profileImageUrl);
		this.birthday = birthday;
		this.serviceAgreement = serviceAgreement;
		this.collectionAgreement = collectionAgreement;
		this.advertisementAgreement = requireNonNull(advertisementAgreement,
			MEMBER_NULL_ADVERTISEMENT_AGREEMENT.getMessage());
		this.socialType = requireNonNull(socialType);
		this.memberType = requireNonNull(memberType);
		this.deletedAt = deletedAt;
	}

	public void updateFcmTokenIfExpired(String fcmToken) {
		if (!isBlank(fcmToken) && !this.fcmToken.equals(fcmToken)) {
			this.fcmToken = fcmToken;
		}
	}

	public void updateProfileImageUrl(String profileImageUrl) {
		if (!isBlank(profileImageUrl) && !this.profileImageUrl.equals(profileImageUrl)) {
			this.profileImageUrl = profileImageUrl;
		}
	}

	public void softDelete() {
		this.socialId = null;
		this.deletedAt = LocalDateTime.now();
	}

	public Integer getAge() {
		if (this.birthday == null) {
			return null;
		}

		LocalDate currentDate = LocalDate.now();
		int age = currentDate.getYear() - this.birthday.getYear();

		// 생일이 아직 지나지 않았으면 나이를 1 줄인다
		if (currentDate.isBefore(this.birthday.withYear(currentDate.getYear()))) {
			age--;
		}

		return age;
	}

	private String validateSocialId(String socialId) {
		if (socialId != null && socialId.length() > SOCIAL_ID_LENGTH) {
			throw new IllegalArgumentException(MEMBER_INVALID_SOCIAL_ID.getMessage());
		}

		return socialId;
	}

	private String validateEmail(String email) {
		if (isBlank(email) || email.length() > EMAIL_LENGTH) {
			throw new IllegalArgumentException(MEMBER_INVALID_EMAIL.getMessage());
		}

		return email;
	}

	private String validateName(String name) {
		if (isBlank(name) || name.length() > NAME_LENGTH) {
			throw new IllegalArgumentException(MEMBER_INVALID_NAME.getMessage());
		}

		return name;
	}

	private String validateProfileImageUrl(String profileImageUrl) {
		if (isBlank(profileImageUrl) || profileImageUrl.length() > PROFILE_IMAGE_URL_LENGTH) {
			throw new IllegalArgumentException(MEMBER_INVALID_PROFILE_IMAGE_URL.getMessage());
		}

		return profileImageUrl;
	}

	private void validateRequiredAgreements(Boolean serviceAgreement, Boolean collectionAgreement) {
		if (serviceAgreement == null || !serviceAgreement) {
			throw new IllegalArgumentException(MEMBER_INVALID_SERVICE_AGREEMENT.getMessage());
		}
		if (collectionAgreement == null || !collectionAgreement) {
			throw new IllegalArgumentException(MEMBER_INVALID_COLLECTION_AGREEMENT.getMessage());
		}
	}
}
