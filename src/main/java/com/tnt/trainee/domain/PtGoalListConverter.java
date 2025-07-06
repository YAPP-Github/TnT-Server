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

		try {
			return objectMapper.writeValueAsString(attribute);
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
			return objectMapper.readValue(dbData,
				objectMapper.getTypeFactory().constructCollectionType(List.class, PtGoal.class));
		} catch (JsonProcessingException e) {
			throw new TnTException(FAILED_TO_CONVERT_JSON, e);
		}
	}
}
