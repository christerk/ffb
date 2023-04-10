package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.model.stadium.OnPitchEnhancement;

import java.awt.image.BufferedImage;

public class FieldLayerEnhancements extends FieldLayer {
	public FieldLayerEnhancements(FantasyFootballClient pClient, DimensionProvider dimensionProvider, FontCache fontCache) {
		super(pClient, dimensionProvider, fontCache);
	}

	public void addEnhancement(OnPitchEnhancement enhancement) {
		BufferedImage icon = getClient().getUserInterface().getIconCache().getIconByProperty(enhancement.getIconProperty());
		draw(icon, enhancement.getCoordinate(), 1.0f);
	}

	public void removeEnhancement(OnPitchEnhancement enhancement) {
		clear(enhancement.getCoordinate(), true);
	}

	@Override
	public void init() {
		clear(true);
		getClient().getGame().getFieldModel().getOnPitchEnhancements().forEach(this::addEnhancement);
	}
}
