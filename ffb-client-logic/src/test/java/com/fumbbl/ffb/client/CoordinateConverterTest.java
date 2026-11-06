package com.fumbbl.ffb.client;

import com.fumbbl.ffb.FieldCoordinate;
import org.junit.jupiter.api.Test;

import java.awt.Canvas;
import java.awt.Point;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CoordinateConverterTest {

	@Test
	void delegatesMouseEventPointToPitchViewport() {
		PitchViewport pitchViewport = mock(PitchViewport.class);
		CoordinateConverter coordinateConverter = new CoordinateConverter(pitchViewport);
		FieldCoordinate expected = new FieldCoordinate(1, 0);

		given(pitchViewport.toFieldCoordinate(new Point(30, 1))).willReturn(expected);

		FieldCoordinate actual = coordinateConverter.getFieldCoordinate(mouseEventAt(30, 1));

		assertEquals(expected, actual);
		verify(pitchViewport).toFieldCoordinate(new Point(30, 1));
	}

	private MouseEvent mouseEventAt(int x, int y) {
		return new MouseEvent(new Canvas(), MouseEvent.MOUSE_MOVED, 0, 0, x, y, 0, false);
	}
}