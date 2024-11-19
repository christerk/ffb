package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.RenderContext;

import javax.swing.Icon;

public class JMenuItem extends javax.swing.JMenuItem {
	public JMenuItem(DimensionProvider dimensionProvider, String name, RenderContext renderContext) {
		super(name);
		dimensionProvider.scaleFont(this, renderContext);
	}

	public JMenuItem(DimensionProvider dimensionProvider, String text, int mnemonic, RenderContext renderContext) {
		super(text, mnemonic);
		dimensionProvider.scaleFont(this, renderContext);
	}

	public JMenuItem(DimensionProvider dimensionProvider, String text, Icon icon, RenderContext renderContext) {
		super(text, icon);
		dimensionProvider.scaleFont(this, renderContext);
	}
}
