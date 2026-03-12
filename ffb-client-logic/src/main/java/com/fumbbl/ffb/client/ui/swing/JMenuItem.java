package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;

import javax.swing.Icon;
import javax.swing.SwingConstants;

public class JMenuItem extends javax.swing.JMenuItem {

	public JMenuItem(DimensionProvider dimensionProvider, String name) {
		super(name);
		setup(dimensionProvider);
	}

	public JMenuItem(DimensionProvider dimensionProvider, String text, int mnemonic) {
		super(text, mnemonic);
		setup(dimensionProvider);
	}

	public JMenuItem(DimensionProvider dimensionProvider, String text, Icon icon) {
		super(text, icon);
		setup(dimensionProvider);
	}

	private void setup(DimensionProvider dimensionProvider) {
		setHorizontalAlignment(SwingConstants.LEFT);
		setHorizontalTextPosition(SwingConstants.LEFT);
		setIconTextGap(dimensionProvider.scale(5));
		dimensionProvider.scaleFont(this);
	}
}
