package com.fumbbl.ffb.client;

import com.fumbbl.ffb.IClientPropertyValue;

import static com.fumbbl.ffb.IClientPropertyValue.SETTING_LAYOUT_LANDSCAPE;

public enum ClientLayout {
	LANDSCAPE(false), PORTRAIT(true), SQUARE(true),
	WIDE(false, (double) 57 / 30, 1.25),
	WIDE_FL_1920x1080(false, (double) 57 / 30, 1.25);

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

    public static ClientLayout getClientLayoutForProperty(String layoutPropertyValue) {
        if (IClientPropertyValue.SETTING_LAYOUT_PORTRAIT.equals(layoutPropertyValue))
            return ClientLayout.PORTRAIT;

        if (IClientPropertyValue.SETTING_LAYOUT_SQUARE.equals(layoutPropertyValue))
            return ClientLayout.SQUARE;

        if (IClientPropertyValue.SETTING_LAYOUT_WIDE.equals(layoutPropertyValue))
            return ClientLayout.WIDE;

        if (IClientPropertyValue.SETTING_LAYOUT_WIDE_1920x1080.equals(layoutPropertyValue))
            return ClientLayout.WIDE_FL_1920x1080;

        return LANDSCAPE;
    }

}
