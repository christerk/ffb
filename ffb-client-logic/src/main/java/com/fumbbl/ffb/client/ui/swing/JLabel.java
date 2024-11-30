package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;

import javax.swing.Icon;

public class JLabel extends javax.swing.JLabel {
	public JLabel(DimensionProvider dimensionProvider, String text) {
		super(text);
		dimensionProvider.scaleFont(this);
	}

	public JLabel(DimensionProvider dimensionProvider) {
		super();
		dimensionProvider.scaleFont(this);
	}

	public JLabel(DimensionProvider dimensionProvider, Icon image) {
		super(image);
		dimensionProvider.scaleFont(this);
	}
}
