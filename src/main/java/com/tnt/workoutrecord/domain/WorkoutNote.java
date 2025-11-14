package com.tnt.workoutrecord.domain;

import java.util.List;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class WorkoutNote {

	private String feedback;
	private List<String> imageUrl;

	@Builder
	public WorkoutNote(String feedback, List<String> imageUrls) {
		this.feedback = feedback;
		this.imageUrl = imageUrls;
	}
}
