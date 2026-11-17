package com.fumbbl.ffb.server.request.fumbbl;

import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.util.StringTool;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class FumbblRequestHeaders {

	public Map<String, String> from(Properties properties) {
		if (properties == null) {
			return Collections.emptyMap();
		}
		Map<String, String> headers = new TreeMap<>();
		for (String key : properties.stringPropertyNames()) {
			if (!key.startsWith(IServerProperty.FUMBBL_HEADER_PREFIX)) {
				continue;
			}
			String name = key.substring(IServerProperty.FUMBBL_HEADER_PREFIX.length()).trim();
			String value = properties.getProperty(key).trim();
			if (StringTool.isProvided(name) && StringTool.isProvided(value)) {
				headers.put(name, value);
			}
		}
		return headers;
	}
}
