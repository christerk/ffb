package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;

import javax.swing.Icon;

public class JRadioButtonMenuItem extends javax.swing.JRadioButtonMenuItem {
	public JRadioButtonMenuItem(DimensionProvider dimensionProvider, String name) {
		super(name);
		dimensionProvider.scaleFont(this);
	}

	public JRadioButtonMenuItem(DimensionProvider dimensionProvider, String text, Icon icon) {
		super(text, icon);
		dimensionProvider.scaleFont(this);
	}
}
