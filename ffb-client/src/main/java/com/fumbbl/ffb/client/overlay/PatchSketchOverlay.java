package com.fumbbl.ffb.client.overlay;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.CoordinateConverter;
import com.fumbbl.ffb.model.sketch.Sketch;
import com.fumbbl.ffb.model.sketch.SketchManager;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

public class PatchSketchOverlay implements Overlay {

	private boolean sketching = false;
	private final CoordinateConverter coordinateConverter;
	private final SketchManager sketchManager;
	private final String coach;

	public PatchSketchOverlay(String coach, CoordinateConverter coordinateConverter) {
		this.coordinateConverter = coordinateConverter;
		this.sketchManager = new SketchManager();
		this.coach = coach;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON1) {
			return;
		}
		FieldCoordinate coordinate = coordinateConverter.getFieldCoordinate(e);
		if (coordinate == null) {
			return;
		}
		List<Sketch> sketches = sketchManager.getSketches(coach);
		if (e.getClickCount() > 1) {
			if (!sketching) {
				sketches.add(new Sketch(new Color(0, 200, 0).getRGB()));
			}
			sketching = !sketching;
		} else if (sketching) {
			sketches.get(sketches.size() - 1).addCoordinate(coordinate);
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
