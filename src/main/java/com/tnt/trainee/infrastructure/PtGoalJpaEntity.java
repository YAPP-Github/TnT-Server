package com.tnt.trainee.infrastructure;

import java.time.LocalDateTime;

import com.tnt.common.jpa.BaseTimeEntity;
import com.tnt.trainee.domain.PtGoal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "pt_goal")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PtGoalJpaEntity extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, unique = true)
	private Long id;

	@Column(name = "trainee_id", nullable = false)
	private Long traineeId;

	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "deleted_at", nullable = true)
	private LocalDateTime deletedAt;

	@Builder
	public PtGoalJpaEntity(Long id, Long traineeId, String content, LocalDateTime deletedAt) {
		this.id = id;
		this.traineeId = traineeId;
		this.content = content;
		this.deletedAt = deletedAt;
	}

	public static PtGoalJpaEntity from(PtGoal ptGoal) {
		return PtGoalJpaEntity.builder()
			.id(ptGoal.getId())
			.traineeId(ptGoal.getTraineeId())
			.content(ptGoal.getContent())
			.deletedAt(ptGoal.getDeletedAt())
			.build();
	}

	public PtGoal toModel() {
		return PtGoal.builder()
			.id(id)
			.traineeId(traineeId)
			.content(content)
			.deletedAt(deletedAt)
			.build();
	}
}
