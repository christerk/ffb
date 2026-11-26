package com.fumbbl.ffb.client;

import com.fumbbl.ffb.FieldCoordinate;

import java.awt.Point;

/**
 * Converts setup drag points in client-content coordinates to FieldCoordinate values.
 *
 * This class chooses between pitch and reserve-box hit testing while keeping
 * drag state, selected player handling, and setup commands outside the mapper.
 */

public class SetupDragHitTester {

	private final PitchViewport pitchViewport;
	private final ReserveBoxViewport reserveBoxViewport;

	public SetupDragHitTester(PitchViewport pitchViewport, ReserveBoxViewport reserveBoxViewport) {
		this.pitchViewport = pitchViewport;
		this.reserveBoxViewport = reserveBoxViewport;
	}

	public FieldCoordinate toFieldCoordinate(Point contentPoint, int boxTitleOffset) {
		FieldCoordinate reserveCoordinate = reserveBoxViewport.toReserveCoordinate(contentPoint, boxTitleOffset);
		if (reserveCoordinate != null) {
			return reserveCoordinate;
		}

		return pitchViewport.screenToWorld(contentPoint);
	}
}
