package com.tnt.trainer.infrastructure;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;

import java.time.LocalDateTime;

import com.tnt.common.jpa.BaseTimeEntity;
import com.tnt.member.infrastructure.MemberJpaEntity;
import com.tnt.trainer.domain.Trainer;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
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
@Table(name = "trainer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerJpaEntity extends BaseTimeEntity {

	@Id
	@Tsid
	@Column(name = "id", nullable = false, unique = true)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
	private MemberJpaEntity member;

	@Column(name = "invitation_code", nullable = false)
	private String invitationCode;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Builder
	public TrainerJpaEntity(Long id, MemberJpaEntity member, String invitationCode, LocalDateTime deletedAt) {
		this.id = id;
		this.member = member;
		this.invitationCode = invitationCode;
		this.deletedAt = deletedAt;
	}

	public static TrainerJpaEntity from(Trainer trainer) {
		return TrainerJpaEntity.builder()
			.id(trainer.getId())
			.member(MemberJpaEntity.from(trainer.getMember()))
			.invitationCode(trainer.getInvitationCode())
			.deletedAt(trainer.getDeletedAt())
			.build();
	}

	public Trainer toModel() {
		return Trainer.builder()
			.id(id)
			.member(member.toModel())
			.invitationCode(invitationCode)
			.deletedAt(deletedAt)
			.build();
	}
}
