package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassMechanic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * @author Kalimar
 */
public class FieldLayerRangeGrid extends FieldLayer {

	private FieldCoordinate fCenterCoordinate;

	public FieldLayerRangeGrid(FantasyFootballClient pClient, DimensionProvider dimensionProvider, FontCache fontCache) {
		super(pClient, dimensionProvider, fontCache);
	}

	public boolean drawRangeGrid(FieldCoordinate pCenterCoordinate, boolean pThrowTeamMate) {
		if ((pCenterCoordinate != null) && !pCenterCoordinate.equals(fCenterCoordinate)) {
			PassMechanic mechanic = (PassMechanic) getClient().getGame().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.PASS.name());
			fCenterCoordinate = pCenterCoordinate;
			for (int y = 0; y < FieldCoordinate.FIELD_HEIGHT; y++) {
				for (int x = 0; x < FieldCoordinate.FIELD_WIDTH; x++) {
					FieldCoordinate coordinate = new FieldCoordinate(x, y);
					clear(coordinate, false);
					PassingDistance passingDistance = mechanic.findPassingDistance(getClient().getGame(), fCenterCoordinate,
							coordinate, pThrowTeamMate);
					if (passingDistance != null) {
						markSquare(coordinate, FieldLayerRangeRuler.getColorForPassingDistance(passingDistance));
					}
				}
			}
			addUpdatedArea(new Rectangle(0, 0, getImage().getWidth(), getImage().getHeight()));
			return true;
		} else {
			return false;
		}
	}

	public boolean clearRangeGrid() {
		if (fCenterCoordinate != null) {
			fCenterCoordinate = null;
			clear(true);
			return true;
		} else {
			return false;
		}
	}

	private void markSquare(FieldCoordinate pCoordinate, Color pColor) {
		if ((pCoordinate != null) && FieldCoordinateBounds.FIELD.isInBounds(pCoordinate)) {
			Dimension dimension = dimensionProvider.mapToLocal(pCoordinate);
			int x = dimension.width;
			int y = dimension.height;
			Rectangle bounds = new Rectangle(x + 1, y + 1, dimensionProvider.fieldSquareSize() - 2, dimensionProvider.fieldSquareSize() - 2);
			Graphics2D g2d = getImage().createGraphics();
			g2d.setPaint(pColor);
			g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
			g2d.dispose();
		}
	}
}
