package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.BloodSpot;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.model.FieldModel;

import java.awt.image.BufferedImage;

/**
 * @author Kalimar
 */
public class FieldLayerBloodspots extends FieldLayer {

	public FieldLayerBloodspots(FantasyFootballClient pClient, UiDimensionProvider uiDimensionProvider, PitchDimensionProvider pitchDimensionProvider, FontCache fontCache) {
		super(pClient, uiDimensionProvider, pitchDimensionProvider, fontCache);
	}

	public void drawBloodspot(BloodSpot pBloodspot) {
		if (!IClientPropertyValue.SETTING_CRATERS_AND_BLOODSPOTS_HIDE
			.equals(getClient().getProperty(CommonProperty.SETTING_SHOW_CRATERS_AND_BLOODSPOTS))) {
			BufferedImage icon = getClient().getUserInterface().getIconCache().getIcon(pBloodspot, pitchDimensionProvider);
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
