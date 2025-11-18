package com.tnt.workoutrecord.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tnt.workoutrecord.application.repository.RoutineRepository;
import com.tnt.workoutrecord.application.repository.SetRepository;
import com.tnt.workoutrecord.application.repository.WorkoutRecordRepository;
import com.tnt.workoutrecord.domain.Routine;
import com.tnt.workoutrecord.domain.Set;
import com.tnt.workoutrecord.domain.WorkoutNote;
import com.tnt.workoutrecord.domain.WorkoutRecord;
import com.tnt.workoutrecord.dto.request.CreateWorkoutRecordRequest;
import com.tnt.workoutrecord.dto.request.UpdateWorkoutRecordRequest;
import com.tnt.workoutrecord.dto.response.GetWorkoutRecordResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkoutRecordService {

	private final SetRepository setRepository;
	private final RoutineRepository routineRepository;
	private final WorkoutRecordRepository workoutRecordRepository;

	@Transactional
	public void createWorkoutRecord(Long memberId, CreateWorkoutRecordRequest request, List<String> imageUrls) {
		WorkoutNote workoutNote = WorkoutNote.builder().feedback(request.feedback()).imageUrls(imageUrls).build();

		WorkoutRecord workoutRecord = WorkoutRecord.builder()
			.memberId(memberId)
			.ptLessonId(request.ptLessonId())
			.date(request.date())
			.workoutNote(workoutNote)
			.recordType(request.recordType())
			.build();

		WorkoutRecord savedWorkoutRecord = workoutRecordRepository.save(workoutRecord);

		List<Routine> routines = request.routines().stream()
			.map(routineInfo -> Routine.builder()
				.workoutRecordId(savedWorkoutRecord.getId())
				.workoutId(routineInfo.workoutId())
				.build())
			.toList();

		List<Routine> savedRoutines = routineRepository.saveAll(routines);

		for (int i = 0; i < savedRoutines.size(); i++) {
			Routine savedRoutine = savedRoutines.get(i);
			CreateWorkoutRecordRequest.RoutineInfo routineInfo = request.routines().get(i);

			List<Set> sets = routineInfo.sets().stream()
				.map(setInfo -> Set.builder()
					.routineId(savedRoutine.getId())
					.durationMinutes(setInfo.durationMinutes())
					.repetition(setInfo.repetition())
					.weight(setInfo.weight())
					.build())
				.toList();

			setRepository.saveAll(sets);
		}
	}

	@Transactional(readOnly = true)
	public void validateImageCount(Long workoutRecordId, int toDeleteCount, int newImageCount) {
		WorkoutRecord workoutRecord = workoutRecordRepository.findById(workoutRecordId);

		int existingImageCount = 0;

		if (workoutRecord.getWorkoutNote() != null && workoutRecord.getWorkoutNote().getImageUrl() != null) {
			existingImageCount = workoutRecord.getWorkoutNote().getImageUrl().size();
		}

		int finalImageCount = existingImageCount - toDeleteCount + newImageCount;

		if (finalImageCount > 6) {
			throw new IllegalArgumentException(
				String.format("이미지는 최대 6장까지 등록 가능합니다. (현재: %d장, 삭제: %d장, 추가: %d장 = 최종: %d장)",
					existingImageCount, toDeleteCount, newImageCount, finalImageCount)
			);
		}
	}

	@Transactional(readOnly = true)
	public GetWorkoutRecordResponse getWorkoutRecord(Long workoutRecordId) {
		WorkoutRecord workoutRecord = workoutRecordRepository.findById(workoutRecordId);
		List<Routine> routines = routineRepository.findAllByWorkoutRecordId(workoutRecordId);
		List<Long> routineIds = routines.stream().map(Routine::getId).toList();
		List<Set> sets = setRepository.findAllByRoutineIds(routineIds);

		// Routine별로 Set 그룹화
		Map<Long, List<Set>> setsByRoutineId = sets.stream().collect(Collectors.groupingBy(Set::getRoutineId));

		List<GetWorkoutRecordResponse.RoutineResponse> routineResponses = routines.stream()
			.map(routine -> {
				List<Set> routineSets = setsByRoutineId.getOrDefault(routine.getId(), List.of());

				List<GetWorkoutRecordResponse.SetResponse> setResponses = routineSets.stream()
					.map(set -> new GetWorkoutRecordResponse.SetResponse(
						set.getId(),  // setId
						set.getDurationMinutes(),
						set.getRepetition(),
						set.getWeight()
					))
					.toList();

				return new GetWorkoutRecordResponse.RoutineResponse(
					routine.getId(),  // routineId
					routine.getWorkoutId(),
					setResponses);
			})
			.toList();

		WorkoutNote workoutNote = workoutRecord.getWorkoutNote();

		return new GetWorkoutRecordResponse(
			workoutRecord.getId(),  // workoutRecordId
			workoutRecord.getMemberId(),
			workoutRecord.getPtLessonId(),
			workoutRecord.getDate(),
			workoutRecord.getRecordType(),
			workoutNote != null ? workoutNote.getFeedback() : null,
			workoutNote != null ? workoutNote.getImageUrl() : List.of(),
			routineResponses
		);
	}

	@Transactional
	public void updateWorkoutRecord(Long memberId, Long workoutRecordId, UpdateWorkoutRecordRequest request,
		List<String> newImageUrls, List<String> imageUrlsToDelete) {
		WorkoutRecord workoutRecord = workoutRecordRepository.findById(workoutRecordId);

		// 기존 이미지 URL 리스트 가져오기
		List<String> existingImageUrls = new ArrayList<>();

		if (workoutRecord.getWorkoutNote() != null && workoutRecord.getWorkoutNote().getImageUrl() != null) {
			existingImageUrls = new ArrayList<>(workoutRecord.getWorkoutNote().getImageUrl());
		}

		// 삭제할 이미지 제거
		if (imageUrlsToDelete != null && !imageUrlsToDelete.isEmpty()) {
			existingImageUrls.removeAll(imageUrlsToDelete);
		}

		// 새 이미지 추가
		if (newImageUrls != null && !newImageUrls.isEmpty()) {
			existingImageUrls.addAll(newImageUrls);
		}

		WorkoutNote updatedWorkoutNote = WorkoutNote.builder()
			.feedback(request.feedback())
			.imageUrls(existingImageUrls)
			.build();

		// 기존 Routines와 Sets 삭제
		List<Routine> oldRoutines = routineRepository.findAllByWorkoutRecordId(workoutRecordId);
		List<Long> oldRoutineIds = oldRoutines.stream().map(Routine::getId).toList();

		setRepository.deleteAllByRoutineIds(oldRoutineIds);
		routineRepository.deleteAllByWorkoutRecordId(workoutRecordId);

		WorkoutRecord updatedWorkoutRecord = WorkoutRecord.builder()
			.id(workoutRecordId)
			.memberId(memberId)
			.ptLessonId(workoutRecord.getPtLessonId())
			.date(workoutRecord.getDate())
			.workoutNote(updatedWorkoutNote)
			.recordType(workoutRecord.getRecordType())
			.build();

		workoutRecordRepository.save(updatedWorkoutRecord);

		List<Routine> newRoutines = request.routines().stream()
			.map(routineInfo -> Routine.builder()
				.workoutRecordId(workoutRecordId)
				.workoutId(routineInfo.workoutId())
				.build())
			.toList();

		List<Routine> savedRoutines = routineRepository.saveAll(newRoutines);

		for (int i = 0; i < savedRoutines.size(); i++) {
			Routine savedRoutine = savedRoutines.get(i);
			UpdateWorkoutRecordRequest.RoutineInfo routineInfo = request.routines().get(i);

			List<Set> sets = routineInfo.sets().stream()
				.map(setInfo -> Set.builder()
					.routineId(savedRoutine.getId())
					.durationMinutes(setInfo.durationMinutes())
					.repetition(setInfo.repetition())
					.weight(setInfo.weight())
					.build())
				.toList();

			setRepository.saveAll(sets);
		}
	}

	@Transactional
	public void deleteWorkoutRecord(Long workoutRecordId) {
		List<Routine> routines = routineRepository.findAllByWorkoutRecordId(workoutRecordId);
		List<Long> routineIds = routines.stream().map(Routine::getId).toList();

		setRepository.deleteAllByRoutineIds(routineIds);

		routineRepository.deleteAllByWorkoutRecordId(workoutRecordId);

		workoutRecordRepository.deleteById(workoutRecordId);
	}

}
