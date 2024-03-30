package com.fumbbl.ffb.model.stadium;

import com.fumbbl.ffb.FieldCoordinate;

public interface OnPitchEnhancement {
	FieldCoordinate getCoordinate();

	String getIconProperty();

	OnPitchEnhancement transform();
}
