package com.tnt.trainee.infrastructure;

import java.time.LocalDateTime;

import com.tnt.common.jpa.BaseTimeEntity;
import com.tnt.trainee.domain.Diet;
import com.tnt.trainee.domain.DietType;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "diet")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietJpaEntity extends BaseTimeEntity {

	@Id
	@Tsid
	@Column(name = "id", nullable = false, unique = true)
	private Long id;

	@Column(name = "trainee_id", nullable = false)
	private Long traineeId;

	@Column(name = "date", nullable = false)
	private LocalDateTime date;

	@Column(name = "diet_image_url", nullable = true)
	private String dietImageUrl;

	@Column(name = "memo", nullable = false)
	private String memo;

	@Column(name = "deleted_at", nullable = true)
	private LocalDateTime deletedAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "diet_type", nullable = false)
	private DietType dietType;

	@Builder
	public DietJpaEntity(Long id, Long traineeId, LocalDateTime date, String dietImageUrl, String memo,
		DietType dietType, LocalDateTime deletedAt) {
		this.id = id;
		this.traineeId = traineeId;
		this.date = date;
		this.dietImageUrl = dietImageUrl;
		this.memo = memo;
		this.dietType = dietType;
		this.deletedAt = deletedAt;
	}

	public static DietJpaEntity from(Diet diet) {
		return DietJpaEntity.builder()
			.id(diet.getId())
			.traineeId(diet.getTraineeId())
			.date(diet.getDate())
			.dietImageUrl(diet.getDietImageUrl())
			.memo(diet.getMemo())
			.dietType(diet.getDietType())
			.deletedAt(diet.getDeletedAt())
			.build();
	}

	public Diet toModel() {
		return Diet.builder()
			.id(id)
			.traineeId(traineeId)
			.date(date)
			.dietImageUrl(dietImageUrl)
			.memo(memo)
			.dietType(dietType)
			.deletedAt(deletedAt)
			.build();
	}
}
