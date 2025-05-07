package com.tnt.member.dto;

public record UpdateProfile(
	String currentImageUrl,
	String changeImageUrl,
	boolean removeCurrentImage,
	boolean isCurrentImageDefault,
	boolean changeImageIsDefault
) {

}
