package com.fumbbl.ffb.client.overlay;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.CoordinateConverter;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.model.sketch.ClientSketchManager;
import com.fumbbl.ffb.model.sketch.Sketch;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Optional;

public class PathSketchOverlay implements Overlay {

	private final CoordinateConverter coordinateConverter;
	private final ClientSketchManager sketchManager;
	private final FieldComponent fieldComponent;

	public PathSketchOverlay(CoordinateConverter coordinateConverter, FieldComponent fieldComponent, ClientSketchManager sketchManager) {
		this.coordinateConverter = coordinateConverter;
		this.sketchManager = sketchManager;
		this.fieldComponent = fieldComponent;
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
			sketchManager.clear();
			drawSketches();
			return;
		}
		FieldCoordinate coordinate = coordinateConverter.getFieldCoordinate(e);
		if (coordinate == null) {
			return;
		}
		Optional<Sketch> activeSketch = sketchManager.activeSketch();
		if (e.getClickCount() > 1) {
			if (activeSketch.isPresent()) {
				sketchManager.finishSketch(coordinate);
			} else {
				sketchManager.create(coordinate, new Color(0, 200, 0).getRGB());
			}
			drawSketches();
		} else if (activeSketch.isPresent()) {
			sketchManager.add(coordinate);
			drawSketches();
		}
	}

	private void drawSketches() {
		fieldComponent.getLayerSketches().draw();
		fieldComponent.refresh();
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
