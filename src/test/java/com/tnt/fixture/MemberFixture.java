package com.tnt.fixture;

import static com.tnt.domain.member.MemberType.TRAINEE;
import static com.tnt.domain.member.MemberType.TRAINER;
import static com.tnt.domain.member.MemberType.UNREGISTERED;
import static com.tnt.domain.member.SocialType.APPLE;
import static com.tnt.domain.member.SocialType.KAKAO;

import java.time.LocalDate;

import com.tnt.domain.member.Member;

public final class MemberFixture {

	public static Member getTrainerMember1() {
		String socialId = "1234567890";
		String email = "abc@gmail.com";
		String name = "김영명";
		String fcmToken = "fcmToken";
		LocalDate birthday = LocalDate.of(2023, 1, 1);
		String profileImageUrl = "https://profile.com/1234567890";

		return Member.builder()
			.socialId(socialId)
			.email(email)
			.name(name)
			.birthday(birthday)
			.fcmToken(fcmToken)
			.profileImageUrl(profileImageUrl)
			.serviceAgreement(true)
			.collectionAgreement(true)
			.advertisementAgreement(true)
			.socialType(KAKAO)
			.memberType(TRAINER)
			.build();
	}

	public static Member getTrainerMemberWithId1() {
		Long id = 1L;
		String socialId = "543251235";
		String email = "efgh@gmail.com";
		String name = "김정호";
		String fcmToken = "fcmToken123";
		LocalDate birthday = LocalDate.of(1997, 10, 1);
		String profileImageUrl = "https://profile.com/1231234";

		return Member.builder()
			.id(id)
			.socialId(socialId)
			.email(email)
			.name(name)
			.birthday(birthday)
			.fcmToken(fcmToken)
			.profileImageUrl(profileImageUrl)
			.serviceAgreement(true)
			.collectionAgreement(true)
			.advertisementAgreement(true)
			.socialType(KAKAO)
			.memberType(TRAINER)
			.build();
	}

	public static Member getTraineeMember1() {
		String socialId = "987676554122";
		String email = "wqert@gmail.com";
		String name = "조만제";
		String fcmToken = "fcmToken3333";
		LocalDate birthday = LocalDate.of(2019, 12, 12);
		String profileImageUrl = "https://profile.com/120847210";

		return Member.builder()
			.socialId(socialId)
			.email(email)
			.name(name)
			.fcmToken(fcmToken)
			.birthday(birthday)
			.profileImageUrl(profileImageUrl)
			.serviceAgreement(true)
			.collectionAgreement(true)
			.advertisementAgreement(true)
			.socialType(KAKAO)
			.memberType(TRAINEE)
			.build();
	}

	public static Member getTraineeMember2() {
		String socialId = "21412e1e12";
		String email = "wqertaba@gmail.com";
		String name = "박민서";
		String fcmToken = "fcmToken3333";
		LocalDate birthday = LocalDate.of(2001, 12, 1);
		String profileImageUrl = "https://profile.com/werwerwvw123";

		return Member.builder()
			.socialId(socialId)
			.email(email)
			.name(name)
			.fcmToken(fcmToken)
			.birthday(birthday)
			.profileImageUrl(profileImageUrl)
			.serviceAgreement(true)
			.collectionAgreement(true)
			.advertisementAgreement(true)
			.socialType(KAKAO)
			.memberType(UNREGISTERED)
			.build();
	}

	public static Member getTraineeMember3() {
		String socialId = "325132512345";
		String email = "rando@gmail.com";
		String name = "김혜린";
		String fcmToken = "fcmToken01010";
		LocalDate birthday = LocalDate.of(2009, 12, 5);
		String profileImageUrl = "https://profile.com/432974tf";

		return Member.builder()
			.socialId(socialId)
			.email(email)
			.name(name)
			.fcmToken(fcmToken)
			.birthday(birthday)
			.profileImageUrl(profileImageUrl)
			.serviceAgreement(true)
			.collectionAgreement(true)
			.advertisementAgreement(true)
			.socialType(APPLE)
			.memberType(TRAINEE)
			.build();
	}

	public static Member getTraineeMember4() {
		String socialId = "76765540";
		String email = "bvfdf@gmail.com";
		String name = "김철수";
		String fcmToken = "fcmToken123214e";
		LocalDate birthday = LocalDate.of(2000, 12, 12);
		String profileImageUrl = "https://profile.com/645344510";

		return Member.builder()
			.socialId(socialId)
			.email(email)
			.name(name)
			.fcmToken(fcmToken)
			.birthday(birthday)
			.profileImageUrl(profileImageUrl)
			.serviceAgreement(true)
			.collectionAgreement(true)
			.advertisementAgreement(true)
			.socialType(APPLE)
			.memberType(TRAINEE)
			.build();
	}

	public static Member getTraineeMemberWithId1() {
		String socialId = "9876765541";
		String email = "wqertaaa@gmail.com";
		String name = "조만제";
		String fcmToken = "fcmToken";
		LocalDate birthday = LocalDate.of(2000, 12, 12);
		String profileImageUrl = "https://profile.com/151512412";

		return Member.builder()
			.id(2L)
			.socialId(socialId)
			.email(email)
			.name(name)
			.fcmToken(fcmToken)
			.birthday(birthday)
			.profileImageUrl(profileImageUrl)
			.serviceAgreement(true)
			.collectionAgreement(true)
			.advertisementAgreement(true)
			.socialType(KAKAO)
			.memberType(TRAINEE)
			.build();
	}

	public static Member getTraineeMemberWithId2() {
		String socialId = "13443124325";
		String email = "email@gmail.com";
		String name = "홍길동";
		String fcmToken = "fcmToken1111";
		LocalDate birthday = LocalDate.of(1999, 4, 12);
		String profileImageUrl = "https://profile.com/kg23rt23";

		return Member.builder()
			.id(3L)
			.socialId(socialId)
			.email(email)
			.name(name)
			.fcmToken(fcmToken)
			.birthday(birthday)
			.profileImageUrl(profileImageUrl)
			.serviceAgreement(true)
			.collectionAgreement(true)
			.advertisementAgreement(true)
			.socialType(KAKAO)
			.memberType(UNREGISTERED)
			.build();
	}
}
