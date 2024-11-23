package com.fumbbl.ffb.client;

public enum ClientLayout {
	LANDSCAPE(false, 1.0), PORTRAIT(true, 1.0), SQUARE(true, 1.0), WIDE(false, (double) 57 / 30);

	private final boolean portrait;

	private final double pitchScale;

	ClientLayout(boolean portrait, double pitchScale) {
		this.portrait = portrait;
		this.pitchScale = pitchScale;
	}

	public boolean isPortrait() {
		return portrait;
	}

	public double getPitchScale() {
		return pitchScale;
	}
}
