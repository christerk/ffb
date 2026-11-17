package com.fumbbl.ffb.server.request.fumbbl;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FumbblRequestHeadersTest {

	private final FumbblRequestHeaders headers = new FumbblRequestHeaders();

	@Test
	void returnsAllConfiguredHeaders() {
		Properties properties = new Properties();
		properties.setProperty("fumbbl.header.X-Ffb-Key", " secret ");
		properties.setProperty("fumbbl.header.X-Ffb-Server", "live");
		properties.setProperty("fumbbl.base", "http://fumbbl.com");

		Map<String, String> expected = new HashMap<>();
		expected.put("X-Ffb-Key", "secret");
		expected.put("X-Ffb-Server", "live");

		assertEquals(expected, headers.from(properties));
	}

	@Test
	void ignoresEntriesWithoutNameOrValue() {
		Properties properties = new Properties();
		properties.setProperty("fumbbl.header.", "secret");
		properties.setProperty("fumbbl.header.X-Ffb-Key", " ");

		assertTrue(headers.from(properties).isEmpty());
	}
}
