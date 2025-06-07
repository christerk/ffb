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
import com.fumbbl.ffb.model.sketch.SketchState;
import com.fumbbl.ffb.util.StringTool;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
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
		draw(new SketchState(sketchManager.getAllSketches()));
	}

	public void draw(SketchState sketchState) {
		List<Sketch> sketches = sketchState.getSketches();
		clear(true);
		Graphics2D graphics2D = getImage().createGraphics();
		Stroke stroke = new BasicStroke(pitchDimensionProvider.dimension(Component.SKETCH_STROKE).width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
		graphics2D.setStroke(stroke);
		Dimension sketchEnd = pitchDimensionProvider.dimension(Component.SKETCH_CIRCLE);
		sketches.forEach(sketch -> {

			Color paint = new Color(sketch.getRgb());
			Color highlightPaint = new Color(paint.getRed(), paint.getGreen(), paint.getBlue(), 128);
			if (sketchState.getHighlightIds().contains(sketch.getId())) {
				graphics2D.setPaint(highlightPaint);
			} else {
				graphics2D.setPaint(paint);
			}
			graphics2D.setFont(fontCache.font(Font.BOLD, 14, pitchDimensionProvider));

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
				if (StringTool.isProvided(sketch.getLabel())) {
					addLabel(graphics2D, sketch.getLabel(), xPoints, yPoints, 1, 0, 0);
					addLabel(graphics2D, sketch.getLabel(), xPoints, yPoints, nPoints - 2, nPoints - 1, nPoints - 1);
				}

			} else {
				if (StringTool.isProvided(sketch.getLabel())) {
					addLabel(graphics2D, sketch.getLabel(), xPoints, yPoints, 0, 0, 0);
				}
			}

			FieldCoordinate previewCoordinate = sketchState.getPreviewCoordinate();
			if (sketch.getId().equals(sketchState.getActiveSketchId())  && previewCoordinate != sketch.getPath().getLast() && previewCoordinate != null) {
				Dimension dimension = pitchDimensionProvider.mapToLocal(previewCoordinate, true);
				graphics2D.setPaint(highlightPaint);
				graphics2D.drawLine(
					xPoints[nPoints - 1], yPoints[nPoints - 1],
					dimension.width, dimension.height
				);
			}
		});
		graphics2D.dispose();
	}

	private void addLabel(Graphics2D graphics2D, String label, int[] xPoints, int[] yPoints, int startIndex, int endIndex, int anchorIndex) {
		int xStart = xPoints[startIndex];
		int yStart = yPoints[startIndex];
		int xEnd = xPoints[endIndex];
		int yEnd = yPoints[endIndex];
		LabelOffset labelOffset = labelPosition(xStart, yStart, xEnd, yEnd);
		printLabel(graphics2D, label, labelCenter(xPoints[anchorIndex], yPoints[anchorIndex], labelOffset));
	}


	private LabelOffset labelPosition(int xStart, int yStart, int xEnd, int yEnd) {
		if (xStart == xEnd && yStart == yEnd) {
			return LabelOffset.BOTTOM_RIGHT;
		}
		if (xStart <= xEnd && yStart <= yEnd) {
			return LabelOffset.BOTTOM_RIGHT;
		} else if (xStart > xEnd && yStart <= yEnd) {
			return LabelOffset.BOTTOM_LEFT;
		} else if (xStart >= xEnd) {
			return LabelOffset.TOP_LEFT;
		} else {
			return LabelOffset.TOP_RIGHT;
		}
	}

	private Dimension labelCenter(int x, int y, LabelOffset labelOffset) {
		int offset = pitchDimensionProvider.dimension(Component.FIELD_SQUARE).width / 2;
		switch (labelOffset) {
			case TOP_LEFT:
				return new Dimension(x - offset, y - offset / 2);
			case BOTTOM_RIGHT:
				return new Dimension(x, y + offset);
			case BOTTOM_LEFT:
				return new Dimension(x - offset, y + offset);
			default: // TOP_RIGHT
				return new Dimension(x, y - offset / 2);
		}
	}

	private void printLabel(Graphics2D graphics2D, String label, Dimension center) {
		graphics2D.drawString(label, center.width, center.height);
	}

	private enum LabelOffset {
		TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT, TOP_RIGHT
	}
}
