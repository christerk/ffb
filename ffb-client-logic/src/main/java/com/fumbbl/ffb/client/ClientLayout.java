package com.fumbbl.ffb.client;

public enum ClientLayout {
	LANDSCAPE(false), PORTRAIT(true), SQUARE(true),
	WIDE(false, (double) 57 / 30, 1.25);

	private final boolean portrait;
	private final double pitchScale;
	private final double dugoutScale;

	ClientLayout(boolean portrait) {
		this(portrait, 1.0, 1.0);
	}

	ClientLayout(boolean portrait, double pitchScale, double dugoutScale) {
		this.portrait = portrait;
		this.pitchScale = pitchScale;
		this.dugoutScale = dugoutScale;
	}

	public boolean isPortrait() {
		return portrait;
	}

	public double getPitchScale() {
		return pitchScale;
	}

	public double getDugoutScale() {
		return dugoutScale;
	}
}
