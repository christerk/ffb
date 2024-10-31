package com.fumbbl.ffb.client;

public enum ClientLayout {
	LANDSCAPE(false), PORTRAIT(true), SQUARE(true), WIDE(false);

	private final boolean portrait;

	ClientLayout(boolean portrait) {
		this.portrait = portrait;
	}

	public boolean isPortrait() {
		return portrait;
	}
}
