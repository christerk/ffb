package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.UiDimensionProvider;
import com.fumbbl.ffb.model.sketch.ClientSketchManager;
import com.fumbbl.ffb.model.sketch.Sketch;

import java.awt.*;
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
		draw();
	}

	public void draw() {
		List<Sketch> sketches = sketchManager.getAllSketches();
		clear(true);
		Graphics2D graphics2D = getImage().createGraphics();
		Stroke stroke = new BasicStroke(pitchDimensionProvider.dimension(Component.SKETCH_STROKE).width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
		graphics2D.setStroke(stroke);
		Dimension sketchEnd = pitchDimensionProvider.dimension(Component.SKETCH_CIRCLE);
		sketches.forEach(sketch -> {
			graphics2D.setPaint(new Color(sketch.getRgb()));
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
				int x = xPoints[nPoints - 1];
				int y = yPoints[nPoints - 1];
				double angle = incomingAngle(xPoints[nPoints - 2], yPoints[nPoints - 2], x, y);

				int[] xTriangle = new int[3];
				int[] yTriangle = new int[3];

				xTriangle[0] = x;
				yTriangle[0] = y;

				int legLength = pitchDimensionProvider.dimension(Component.SKETCH_TRIANGLE).width;
				int legAngle = pitchDimensionProvider.unscaledDimension(Component.SKETCH_TRIANGLE).height;

				double angleRad = Math.toRadians(angle);
				double angle1 = angleRad - Math.toRadians(legAngle);
				double angle2 = angleRad + Math.toRadians(legAngle);

				xTriangle[1] = (int) (x - Math.cos(angle1) * legLength);
				yTriangle[1] = (int) (y + Math.sin(angle1) * legLength);

				xTriangle[2] = (int) (x - Math.cos(angle2) * legLength);
				yTriangle[2] = (int) (y + Math.sin(angle2) * legLength);

				graphics2D.fillPolygon(xTriangle, yTriangle, 3);
			}
		});
		graphics2D.dispose();
	}

	private double incomingAngle(int xFrom, int yFrom, int xTo, int yTo) {
		int xDiff = xTo - xFrom;
		int yDiff = yFrom - yTo; // y==0 is the top line of the component
		return Math.toDegrees(Math.atan2(yDiff, xDiff));
	}
}
