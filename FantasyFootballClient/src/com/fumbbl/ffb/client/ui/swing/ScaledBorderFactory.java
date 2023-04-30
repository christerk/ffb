package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;

import javax.swing.border.TitledBorder;

public class ScaledBorderFactory {
	private ScaledBorderFactory() {
	}

	public static TitledBorder createTitledBorder(DimensionProvider dimensionProvider, String title) {
		TitledBorder border = javax.swing.BorderFactory.createTitledBorder(title);
		return dimensionProvider.scaleFont(border);
	}
}
