package com.fumbbl.ffb;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class IClientPropertyValueTest {

	@ParameterizedTest
	@MethodSource("values")
	public void assertFieldLength(String property) {
		assertTrue(property.length() <= 40, "Name " + property + " is too long for database (40 chars)");
	}

	private static List<String> values() {
		return Arrays.stream(IClientPropertyValue.class.getFields()).map(field -> {
			try {
				return field.get(null);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}).filter(value -> value instanceof String).map(value -> (String) value).collect(Collectors.toList());
	}
}