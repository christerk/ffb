package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.BloodSpot;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.model.FieldModel;

import java.awt.image.BufferedImage;

/**
 * @author Kalimar
 */
public class FieldLayerBloodspots extends FieldLayer {

	public FieldLayerBloodspots(FantasyFootballClient pClient, DimensionProvider dimensionProvider) {
		super(pClient, dimensionProvider);
	}

	public void drawBloodspot(BloodSpot pBloodspot) {
		BufferedImage icon = getClient().getUserInterface().getIconCache().getIcon(pBloodspot);
		draw(icon, pBloodspot.getCoordinate(), 1.0f);
	}

	public void init() {
		clear(true);
		FieldModel fieldModel = getClient().getGame().getFieldModel();
		if (fieldModel != null) {
			BloodSpot[] bloodspots = fieldModel.getBloodSpots();
			for (int i = 0; i < bloodspots.length; i++) {
				drawBloodspot(bloodspots[i]);
			}
		}
	}

}
