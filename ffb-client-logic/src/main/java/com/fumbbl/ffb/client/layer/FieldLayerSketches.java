package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.UiDimensionProvider;
import com.fumbbl.ffb.model.sketch.Sketch;

import java.awt.*;
import java.util.List;

public class FieldLayerSketches extends FieldLayer {
	public FieldLayerSketches(FantasyFootballClient pClient, UiDimensionProvider uiDimensionProvider, PitchDimensionProvider pitchDimensionProvider, FontCache fontCache) {
		super(pClient, uiDimensionProvider, pitchDimensionProvider, fontCache);
	}

	public void draw(List<Sketch> sketchList) {
		clear(true);
		Graphics2D graphics2D = getImage().createGraphics();
		sketchList.forEach(sketch -> {
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
			graphics2D.drawPolyline(xPoints, yPoints, nPoints);
		});
		graphics2D.dispose();
	}
}
