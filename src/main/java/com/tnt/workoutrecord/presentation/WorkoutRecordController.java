package com.tnt.workoutrecord.presentation;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tnt.gateway.config.AuthMember;
import com.tnt.image.application.S3Service;
import com.tnt.workoutrecord.application.WorkoutRecordService;
import com.tnt.workoutrecord.dto.request.CreateWorkoutRecordRequest;
import com.tnt.workoutrecord.dto.request.UpdateWorkoutRecordRequest;
import com.tnt.workoutrecord.dto.response.GetWorkoutRecordResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@Tag(name = "운동 기록", description = "운동 기록 관련 API")
@RestController
@RequestMapping("/workout-records")
@RequiredArgsConstructor
public class WorkoutRecordController {

	private final WorkoutRecordService workoutRecordService;
	private final S3Service s3Service;

	@Operation(summary = "운동 기록 등록 API", description = "JSON 데이터와 이미지 파일(최대 6장)을 함께 전송합니다.")
	@PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(CREATED)
	public void createWorkoutRecord(@AuthMember Long memberId,
		@Parameter(description = "운동 기록 정보", required = true)
		@RequestPart("request") @Valid CreateWorkoutRecordRequest request,

		@Parameter(description = "운동 사진 (최대 6장)", schema = @Schema(type = "array", format = "binary"))
		@Size(max = 6, message = "이미지는 최대 6장까지 등록 가능합니다.")
		@RequestPart(value = "images", required = false) List<MultipartFile> images
	) {
		List<String> imageUrls = s3Service.uploadWorkoutRecordImages(images);

		workoutRecordService.createWorkoutRecord(memberId, request, imageUrls);
	}

	@Operation(summary = "운동 기록 조회 API", description = "운동 기록 상세 정보를 조회합니다.")
	@GetMapping("/{recordId}")
	@ResponseStatus(OK)
	public GetWorkoutRecordResponse getWorkoutRecord(@PathVariable Long recordId) {
		return workoutRecordService.getWorkoutRecord(recordId);
	}

	@Operation(summary = "운동 기록 수정 API",
		description = "운동 기록을 수정합니다. 새 이미지 추가와 기존 이미지 삭제를 동시에 처리하며, 루틴 정보는 전체 교체됩니다.")
	@PutMapping(value = "/{recordId}", consumes = MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(NO_CONTENT)
	public void updateWorkoutRecord(@AuthMember Long memberId,
		@PathVariable Long recordId,

		@Parameter(description = "수정할 운동 기록 정보", required = true)
		@RequestPart("request") @Valid UpdateWorkoutRecordRequest request,

		@Parameter(description = "추가할 운동 사진 (기존 사진과 합쳐 최대 6장)", schema = @Schema(type = "array", format = "binary"))
		@RequestPart(value = "newImages", required = false) List<MultipartFile> newImages
	) {
		// 새 이미지 업로드
		List<String> newImageUrls = s3Service.uploadWorkoutRecordImages(newImages);

		// 삭제할 이미지 S3에서 삭제
		if (request.imageUrlsToDelete() != null && !request.imageUrlsToDelete().isEmpty()) {
			request.imageUrlsToDelete().forEach(s3Service::deleteWorkoutRecordImage);
		}

		workoutRecordService.updateWorkoutRecord(memberId, recordId, request, newImageUrls,
			request.imageUrlsToDelete());
	}

	@Operation(summary = "운동 기록 삭제 API", description = "운동 기록과 관련된 루틴, 세트를 모두 삭제합니다.")
	@DeleteMapping("/{recordId}")
	@ResponseStatus(NO_CONTENT)
	public void deleteWorkoutRecord(@PathVariable Long recordId) {
		// 이미지 URL 조회 후 S3에서 삭제
		GetWorkoutRecordResponse workoutRecord = workoutRecordService.getWorkoutRecord(recordId);

		if (workoutRecord.imageUrls() != null && !workoutRecord.imageUrls().isEmpty()) {
			workoutRecord.imageUrls().forEach(s3Service::deleteWorkoutRecordImage);
		}

		workoutRecordService.deleteWorkoutRecord(recordId);
	}
}
