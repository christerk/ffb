package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.UiDimensionProvider;
import com.fumbbl.ffb.client.overlay.sketch.ClientSketchManager;
import com.fumbbl.ffb.client.overlay.sketch.TriangleCoords;
import com.fumbbl.ffb.model.sketch.Sketch;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Collections;
import java.util.List;

public class FieldLayerSketches extends FieldLayer {

	private final ClientSketchManager sketchManager;

	public FieldLayerSketches(FantasyFootballClient pClient, UiDimensionProvider uiDimensionProvider, PitchDimensionProvider pitchDimensionProvider, FontCache fontCache, ClientSketchManager sketchManager) {
		super(pClient, uiDimensionProvider, pitchDimensionProvider, fontCache);
		this.sketchManager = sketchManager;
	}

	@Override
	public void init() {
		super.init();
		draw(Collections.emptyList());
	}

	public void draw(List<Sketch> highlights) {
		List<Sketch> sketches = sketchManager.getAllSketches();
		clear(true);
		Graphics2D graphics2D = getImage().createGraphics();
		Stroke stroke = new BasicStroke(pitchDimensionProvider.dimension(Component.SKETCH_STROKE).width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
		graphics2D.setStroke(stroke);
		Dimension sketchEnd = pitchDimensionProvider.dimension(Component.SKETCH_CIRCLE);
		sketches.forEach(sketch -> {

			Color paint = new Color(sketch.getRgb());
			if (highlights.contains(sketch)) {
				paint = new Color(paint.getRed(), paint.getGreen(), paint.getBlue(), 128);
			}
			graphics2D.setPaint(paint);
			int nPoints = sketch.getPath().size();
			int[] xPoints = new int[nPoints];
			int[] yPoints = new int[nPoints];
			for (int i = 0; i < nPoints; i++) {
				FieldCoordinate coordinate = sketch.getPath().get(i);
				Dimension dimension = pitchDimensionProvider.mapToLocal(coordinate, true);
				xPoints[i] = dimension.width;
				yPoints[i] = dimension.height;
			}
			graphics2D.fillOval(xPoints[0] - sketchEnd.width / 2, yPoints[0] - sketchEnd.height / 2, sketchEnd.width, sketchEnd.height);
			graphics2D.drawPolyline(xPoints, yPoints, nPoints);
			if (nPoints > 1) {
				int legLength = pitchDimensionProvider.dimension(Component.SKETCH_TRIANGLE).width;
				int legAngle = pitchDimensionProvider.unscaledDimension(Component.SKETCH_TRIANGLE).height;

				TriangleCoords triangle = TriangleCoords.calculate(xPoints[nPoints - 2], yPoints[nPoints - 2], xPoints[nPoints - 1], yPoints[nPoints - 1], legLength, legAngle);

				graphics2D.fillPolygon(triangle.getxCoords(), triangle.getyCoords(), 3);
			}
		});
		graphics2D.dispose();
	}

}
