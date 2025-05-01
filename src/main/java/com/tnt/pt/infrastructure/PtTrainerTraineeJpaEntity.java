package com.tnt.pt.infrastructure;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.tnt.common.jpa.BaseTimeEntity;
import com.tnt.pt.domain.PtTrainerTrainee;
import com.tnt.trainee.infrastructure.TraineeJpaEntity;
import com.tnt.trainer.infrastructure.TrainerJpaEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "pt_trainer_trainee")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PtTrainerTraineeJpaEntity extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, unique = true)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trainer_id", nullable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
	private TrainerJpaEntity trainer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trainee_id", nullable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
	private TraineeJpaEntity trainee;

	@Column(name = "started_at", nullable = false)
	private LocalDate startedAt;

	@Column(name = "finished_pt_count", nullable = false)
	private Integer finishedPtCount;

	@Column(name = "total_pt_count", nullable = false)
	private Integer totalPtCount;

	@Column(name = "deleted_at", nullable = true)
	private LocalDateTime deletedAt;

	@Builder
	public PtTrainerTraineeJpaEntity(Long id, TrainerJpaEntity trainer, TraineeJpaEntity trainee, LocalDate startedAt,
		Integer finishedPtCount, Integer totalPtCount, LocalDateTime deletedAt) {
		this.id = id;
		this.trainer = trainer;
		this.trainee = trainee;
		this.startedAt = startedAt;
		this.finishedPtCount = finishedPtCount;
		this.totalPtCount = totalPtCount;
		this.deletedAt = deletedAt;
	}

	public static PtTrainerTraineeJpaEntity from(PtTrainerTrainee ptTrainerTrainee) {
		return PtTrainerTraineeJpaEntity.builder()
			.id(ptTrainerTrainee.getId())
			.trainer(TrainerJpaEntity.from(ptTrainerTrainee.getTrainer()))
			.trainee(TraineeJpaEntity.from(ptTrainerTrainee.getTrainee()))
			.startedAt(ptTrainerTrainee.getStartedAt())
			.finishedPtCount(ptTrainerTrainee.getFinishedPtCount())
			.totalPtCount(ptTrainerTrainee.getTotalPtCount())
			.deletedAt(ptTrainerTrainee.getDeletedAt())
			.build();
	}

	public PtTrainerTrainee toModel() {
		return PtTrainerTrainee.builder()
			.id(id)
			.trainer(trainer.toModel())
			.trainee(trainee.toModel())
			.startedAt(startedAt)
			.finishedPtCount(finishedPtCount)
			.totalPtCount(totalPtCount)
			.deletedAt(deletedAt)
			.build();
	}
}
