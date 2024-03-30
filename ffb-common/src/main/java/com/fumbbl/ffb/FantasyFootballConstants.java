package com.fumbbl.ffb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FantasyFootballConstants {

	static {
		Properties properties = new Properties();
		try (InputStream stream = FantasyFootballConstants.class.getResourceAsStream("/common.properties")) {
			properties.load(stream);
			VERSION = properties.getProperty("version");
		} catch (IOException e) {
			throw new RuntimeException("Could not load common properties", e);
		}
	}
	public static String VERSION;
}
