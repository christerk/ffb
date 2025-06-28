package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;

import javax.swing.Icon;

public class JMenuItem extends javax.swing.JMenuItem {
	public JMenuItem() {
		super();
	}

	public JMenuItem(DimensionProvider dimensionProvider, String name) {
		super(name);
		dimensionProvider.scaleFont(this);
	}

	public JMenuItem(DimensionProvider dimensionProvider, String text, int mnemonic) {
		super(text, mnemonic);
		dimensionProvider.scaleFont(this);
	}

	public JMenuItem(DimensionProvider dimensionProvider, String text, Icon icon) {
		super(text, icon);
		dimensionProvider.scaleFont(this);
	}
}
