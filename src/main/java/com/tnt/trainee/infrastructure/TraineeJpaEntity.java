package com.tnt.trainee.infrastructure;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.tnt.common.jpa.BaseTimeEntity;
import com.tnt.member.infrastructure.MemberJpaEntity;
import com.tnt.trainee.domain.PtGoal;
import com.tnt.trainee.domain.PtGoalListConverter;
import com.tnt.trainee.domain.Trainee;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "trainee")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TraineeJpaEntity extends BaseTimeEntity {

	@Id
	@Tsid
	@Column(name = "id", nullable = false, unique = true)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
	private MemberJpaEntity member;

	@Column(name = "height", nullable = true)
	private Double height;

	@Column(name = "weight", nullable = true)
	private Double weight;

	@Column(name = "caution_note", nullable = true)
	private String cautionNote;

	@Column(name = "deleted_at", nullable = true)
	private LocalDateTime deletedAt;

	@Convert(converter = PtGoalListConverter.class)
	@Column(name = "pt_goals", columnDefinition = "TEXT", nullable = false)
	private List<PtGoal> ptGoals;

	@Builder
	public TraineeJpaEntity(Long id, MemberJpaEntity member, Double height, Double weight, String cautionNote,
		List<PtGoal> ptGoals, LocalDateTime deletedAt) {
		this.id = id;
		this.member = requireNonNull(member);
		this.height = height;
		this.weight = weight;
		this.cautionNote = cautionNote;
		this.ptGoals = ptGoals != null ? new ArrayList<>(ptGoals) : new ArrayList<>();
		this.deletedAt = deletedAt;
	}

	public static TraineeJpaEntity from(Trainee trainee) {
		return TraineeJpaEntity.builder()
			.id(trainee.getId())
			.member(MemberJpaEntity.from(trainee.getMember()))
			.height(trainee.getHeight())
			.weight(trainee.getWeight())
			.cautionNote(trainee.getCautionNote())
			.ptGoals(trainee.getPtGoals())
			.deletedAt(trainee.getDeletedAt())
			.build();
	}

	public Trainee toModel() {
		return Trainee.builder()
			.id(id)
			.member(member.toModel())
			.height(height)
			.weight(weight)
			.cautionNote(cautionNote)
			.ptGoals(ptGoals)
			.deletedAt(deletedAt)
			.build();
	}
}
