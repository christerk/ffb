package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.UiDimensionProvider;

public class FieldLayerSketches extends FieldLayer {
	public FieldLayerSketches(FantasyFootballClient pClient, UiDimensionProvider uiDimensionProvider, PitchDimensionProvider pitchDimensionProvider, FontCache fontCache) {
		super(pClient, uiDimensionProvider, pitchDimensionProvider, fontCache);
	}
}
