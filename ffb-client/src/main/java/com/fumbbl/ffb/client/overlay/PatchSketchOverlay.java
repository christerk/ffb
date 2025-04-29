package com.fumbbl.ffb.client.overlay;

import com.fumbbl.ffb.client.CoordinateConverter;

import java.awt.event.MouseEvent;

public class PatchSketchOverlay implements Overlay {

	private boolean sketching = false;
	private final CoordinateConverter coordinateConverter;

	public PatchSketchOverlay(CoordinateConverter coordinateConverter) {
		this.coordinateConverter = coordinateConverter;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getClickCount() > 1 && e.getButton() == MouseEvent.BUTTON1) {
			if (sketching) {

			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}
}
