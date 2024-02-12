package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.BloodSpot;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.model.FieldModel;

import java.awt.image.BufferedImage;

/**
 * @author Kalimar
 */
public class FieldLayerBloodspots extends FieldLayer {

	public FieldLayerBloodspots(FantasyFootballClient pClient, DimensionProvider dimensionProvider, FontCache fontCache) {
		super(pClient, dimensionProvider, fontCache);
	}

	public void drawBloodspot(BloodSpot pBloodspot) {
		if (!IClientPropertyValue.SETTING_CRATERS_AND_BLOODSPOTS_HIDE
			.equals(getClient().getProperty(CommonProperty.SETTING_SHOW_CRATERS_AND_BLOODSPOTS))) {
			BufferedImage icon = getClient().getUserInterface().getIconCache().getIcon(pBloodspot);
			draw(icon, pBloodspot.getCoordinate(), 1.0f);
		}
	}

	public void init() {
		clear(true);
		FieldModel fieldModel = getClient().getGame().getFieldModel();
		if (fieldModel != null) {
			BloodSpot[] bloodspots = fieldModel.getBloodSpots();
			for (BloodSpot bloodspot : bloodspots) {
				drawBloodspot(bloodspot);
			}
		}
	}

}
