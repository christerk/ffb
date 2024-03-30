package com.fumbbl.ffb.server;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServerUrlPropertyTest {

	public static final String BASE = "base";
	public static final String BASE_WITH_PORT = "baseWithPort";
	public static final String BASE_WITH_SLASH = "baseWithSlash";
	public static final String PORT = "port";
	public static final String PATH = "path";
	public static final String PATH_WITH_SLASH = "pathWithSlash";
	public static final String PATH_HAS_URL = "pathHasUrl";
	public static final String SLASHES_ONLY = "slashesOnly";

	@Test
	void urlReturnsAssembledValue() {
		ServerUrlProperty property = buildMock(BASE, PORT,  PATH);

		assertEquals("https://host:8000/path", property.url(setupProps()));

	}

	@Test
	void urlHandlesUndefinedPort() {
		ServerUrlProperty property = buildMock(BASE, "unknownPort", PATH);

		assertEquals("https://host/path", property.url(setupProps()));

	}


	@Test
	void urlHandlesUndefinedPath() {
		ServerUrlProperty property = buildMock(BASE, PORT, "unknownPath");

		assertEquals("https://host:8000", property.url(setupProps()));

	}


	@Test
	void urlHandlesSlashAsPathAndPrefix() {
		ServerUrlProperty property = buildMock(BASE, PORT, SLASHES_ONLY);

		assertEquals("https://host:8000", property.url(setupProps()));

	}

	@Test
	void urlIgnoresDuplicateSlashes() {
		ServerUrlProperty property = buildMock(BASE_WITH_SLASH, "unknownPort", PATH_WITH_SLASH);

		assertEquals("https://host/path", property.url(setupProps()));
	}

	@Test
	void urlIgnoresPortIfPresentInBase() {
		ServerUrlProperty property = buildMock(BASE_WITH_PORT, PORT, PATH);

		assertEquals("https://host:8080/path", property.url(setupProps()));

	}


	@Test
	void urlIgnoresOtherValuesIfPathIsFullUrl() {
		ServerUrlProperty property = buildMock(BASE, PORT, PATH_HAS_URL);

		assertEquals("https://otherhost/path", property.url(setupProps()));

	}


	@Test
	void urlRemovesSlashFromBase() {
		ServerUrlProperty property = buildMock(BASE_WITH_SLASH, PORT, PATH);

		assertEquals("https://host:8000/path", property.url(setupProps()));

	}


	private Properties setupProps() {
		Properties props = new Properties();

		props.setProperty(BASE, "https://host");
		props.setProperty(BASE_WITH_PORT, "https://host:8080");
		props.setProperty(BASE_WITH_SLASH, "https://host/");
		props.setProperty(PORT, "8000");
		props.setProperty(PATH, PATH);
		props.setProperty(PATH_WITH_SLASH, "//path");
		props.setProperty(PATH_HAS_URL, "https://otherhost/path");

		return props;
	}

	private ServerUrlProperty buildMock(String baseKey, String portKey, String pathKey) {
		ServerUrlProperty prop = mock(ServerUrlProperty.class);

		when(prop.getBaseKey()).thenReturn(baseKey);
		when(prop.getPortKey()).thenReturn(portKey);
		when(prop.getPathKey()).thenReturn(pathKey);
		when(prop.url(any(Properties.class))).thenCallRealMethod();

		return prop;
	}

}