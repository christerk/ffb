package com.fumbbl.ffb.client;

import java.util.HashMap;
import java.util.Map;

public class FontConfig {

	private final Map<Size, Integer> sizes = new HashMap<>();

	public FontConfig(int small, int medium, int large) {
		sizes.put(Size.SMALL, small);
		sizes.put(Size.MEDIUM, medium);
		sizes.put(Size.LARGE, large);
	}

	public int getSize(Size size) {
		return sizes.get(size);
	}

	public enum Size {
		SMALL,
		MEDIUM,
		LARGE
	}
}
