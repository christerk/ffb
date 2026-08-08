package com.fumbbl.ffb.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StringToolTest {

	@Test
	void capitalizeCapitalizesFirstCharacter() {
		assertEquals("Fireball", StringTool.capitalize("fireball"));
	}

	@Test
	void capitalizeCapitalizesRequestedCharacters() {
		assertEquals("ZAP", StringTool.capitalize("zap", 10));
	}

	@Test
	void capitalizeLeavesInputUnchangedWhenLengthIsNotPositive() {
		assertEquals("fireball", StringTool.capitalize("fireball", 0));
		assertEquals("fireball", StringTool.capitalize("fireball", -1));
	}

	@Test
	void capitalizeReturnsNullForNullInput() {
		assertNull(StringTool.capitalize(null));
	}
}
