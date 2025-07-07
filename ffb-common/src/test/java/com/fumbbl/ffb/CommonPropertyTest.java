package com.fumbbl.ffb;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CommonPropertyTest {

	@ParameterizedTest
	@EnumSource(CommonProperty.class)
	public void assertFieldLength(CommonProperty property) {
		assertTrue(property.getKey().length() <= 40, "Name of " + property.name() + " is too long for database (40 chars)");
	}
}