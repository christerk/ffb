package com.fumbbl.ffb.client;

import com.fumbbl.ffb.FieldCoordinate;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * Converts setup drag points in client-content coordinates to FieldCoordinate values.
 *
 * This class chooses between pitch and reserve-box hit testing while keeping
 * drag state, selected player handling, and setup commands outside the mapper.
 */

public class SetupDragHitTester {

	private final PitchViewport pitchViewport;
	private final ReserveBoxViewport reserveBoxViewport;
	private final PitchDimensionProvider pitchDimensionProvider;

	public SetupDragHitTester(PitchViewport pitchViewport, ReserveBoxViewport reserveBoxViewport,
	                          PitchDimensionProvider pitchDimensionProvider) {
		this.pitchViewport = pitchViewport;
		this.reserveBoxViewport = reserveBoxViewport;
		this.pitchDimensionProvider = pitchDimensionProvider;
	}

	public FieldCoordinate toFieldCoordinate(Point contentPoint, int boxTitleOffset) {
		FieldCoordinate reserveCoordinate = reserveBoxViewport.toReserveCoordinate(contentPoint, boxTitleOffset);
		if (reserveCoordinate != null) {
			return reserveCoordinate;
		}

		return toSetupPitchCoordinate(contentPoint);
	}

	private FieldCoordinate toSetupPitchCoordinate(Point contentPoint) {
		Rectangle pitchBounds = pitchViewport.viewportBounds();
		Dimension fieldSize = pitchViewport.fieldSize();

		int actualX = contentPoint.x - pitchBounds.x;
		int actualY = contentPoint.y - pitchBounds.y;

		if (pitchDimensionProvider.isPitchPortrait()) {
			int mouseX = actualX;
			int mouseY = actualY;
			actualY = mouseX;
			actualX = fieldSize.height - mouseY;
		}

		if ((actualX >= 0) && (actualX < fieldSize.width)
			&& (actualY >= 0) && (actualY < fieldSize.height)) {
			return new FieldCoordinate(
				actualX / pitchViewport.squareSize(),
				actualY / pitchViewport.squareSize()
			);
		}

		return null;
	}
}
