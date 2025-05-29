package com.tnt.member.dto;

public record ProfileUpdate(
	String currentImageUrl,
	String changeImageUrl,
	boolean removeCurrentImage,
	boolean isCurrentImageDefault
) {

}
