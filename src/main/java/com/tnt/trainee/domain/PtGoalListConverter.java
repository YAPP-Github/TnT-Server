package com.tnt.trainee.domain;

import static com.tnt.common.error.model.ErrorMessage.FAILED_TO_CONVERT_JSON;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tnt.common.error.exception.TnTException;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PtGoalListConverter implements AttributeConverter<List<PtGoal>, String> {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(List<PtGoal> attribute) {
		if (attribute == null || attribute.isEmpty()) {
			return "[]";
		}

		// enum name만 추출해서 저장
		List<String> names = attribute.stream().map(Enum::name).toList();

		try {
			return objectMapper.writeValueAsString(names);
		} catch (JsonProcessingException e) {
			throw new TnTException(FAILED_TO_CONVERT_JSON, e);
		}
	}

	@Override
	public List<PtGoal> convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.trim().isEmpty()) {
			return new ArrayList<>();
		}

		try {
			List<String> names = objectMapper.readValue(dbData,
				objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));

			return names.stream().map(PtGoal::valueOf).toList();
		} catch (JsonProcessingException e) {
			throw new TnTException(FAILED_TO_CONVERT_JSON, e);
		}
	}
}
