package com.tnt.pt.infrastructure;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;

import java.time.LocalDateTime;

import com.tnt.common.jpa.BaseTimeEntity;
import com.tnt.pt.domain.PtLesson;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
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
@Table(name = "pt_lesson")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PtLessonJpaEntity extends BaseTimeEntity {

	@Id
	@Tsid
	@Column(name = "id", nullable = false, unique = true)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pt_trainer_trainee_id", nullable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
	private PtTrainerTraineeJpaEntity ptTrainerTrainee;

	@Column(name = "lesson_start", nullable = false)
	private LocalDateTime lessonStart;

	@Column(name = "lesson_end", nullable = false)
	private LocalDateTime lessonEnd;

	@Column(name = "is_completed", nullable = false)
	private Boolean isCompleted;

	@Column(name = "memo", nullable = true)
	private String memo;

	@Column(name = "session", nullable = false)
	private Integer session;

	@Column(name = "deleted_at", nullable = true)
	private LocalDateTime deletedAt;

	@Builder
	public PtLessonJpaEntity(Long id, PtTrainerTraineeJpaEntity ptTrainerTrainee, LocalDateTime lessonStart,
		LocalDateTime lessonEnd, Boolean isCompleted, String memo, Integer session, LocalDateTime deletedAt) {
		this.id = id;
		this.ptTrainerTrainee = ptTrainerTrainee;
		this.lessonStart = lessonStart;
		this.lessonEnd = lessonEnd;
		this.isCompleted = isCompleted;
		this.session = session;
		this.memo = memo;
		this.deletedAt = deletedAt;
	}

	public static PtLessonJpaEntity from(PtLesson ptLesson) {
		return PtLessonJpaEntity.builder()
			.id(ptLesson.getId())
			.ptTrainerTrainee(PtTrainerTraineeJpaEntity.from(ptLesson.getPtTrainerTrainee()))
			.lessonStart(ptLesson.getLessonStart())
			.lessonEnd(ptLesson.getLessonEnd())
			.isCompleted(ptLesson.getIsCompleted())
			.memo(ptLesson.getMemo())
			.session(ptLesson.getSession())
			.deletedAt(ptLesson.getDeletedAt())
			.build();
	}

	public PtLesson toModel() {
		return PtLesson.builder()
			.id(id)
			.ptTrainerTrainee(ptTrainerTrainee.toModel())
			.lessonStart(lessonStart)
			.lessonEnd(lessonEnd)
			.isCompleted(isCompleted)
			.memo(memo)
			.session(session)
			.deletedAt(deletedAt)
			.build();
	}
}
