package com.fumbbl.ffb.client;

import com.fumbbl.ffb.FieldCoordinate;

import java.awt.event.MouseEvent;

public class CoordinateConverter {
	private final PitchViewport pitchViewport;

	public CoordinateConverter(PitchViewport pitchViewport) {
		this.pitchViewport = pitchViewport;
	}

	public FieldCoordinate getFieldCoordinate(MouseEvent pMouseEvent) {
		return pitchViewport.toFieldCoordinate(pMouseEvent.getPoint());
	}
}
