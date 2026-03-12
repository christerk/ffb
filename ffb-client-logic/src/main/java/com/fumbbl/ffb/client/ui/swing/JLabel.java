package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;

import javax.swing.Icon;
import javax.swing.SwingConstants;

public class JLabel extends javax.swing.JLabel {
	public JLabel(DimensionProvider dimensionProvider, String text) {
		super(text);
		setup(dimensionProvider);
	}

	public JLabel(DimensionProvider dimensionProvider) {
		super();
		setup(dimensionProvider);
	}

	public JLabel(DimensionProvider dimensionProvider, Icon image) {
		super(image);
		setup(dimensionProvider);
	}

	private void setup(DimensionProvider dimensionProvider) {
		dimensionProvider.scaleFont(this);
		setHorizontalAlignment(SwingConstants.LEFT);
		setHorizontalTextPosition(SwingConstants.LEFT);
		setIconTextGap(4);
	}

}
