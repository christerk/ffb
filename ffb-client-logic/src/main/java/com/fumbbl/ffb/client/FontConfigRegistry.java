package com.fumbbl.ffb.client;

import java.util.HashMap;
import java.util.Map;

public class FontConfigRegistry {

	private final static FontConfig REGULAR = new FontConfig(11, 12, 13);
	private final static FontConfig LARGER = new FontConfig(14, 15, 16);

	private final Map<ClientLayout, FontConfig>  fontConfigs = new HashMap<ClientLayout, FontConfig>() {{
		put(ClientLayout.LANDSCAPE, REGULAR);
		put(ClientLayout.PORTRAIT, LARGER);
		put(ClientLayout.SQUARE, REGULAR);
		put(ClientLayout.WIDE, REGULAR);
		// put(ClientLayout.FULL_SCREEN, LARGER);
	}};

	public FontConfig getConfig(ClientLayout layout) {
		return fontConfigs.get(layout);
	}
}
