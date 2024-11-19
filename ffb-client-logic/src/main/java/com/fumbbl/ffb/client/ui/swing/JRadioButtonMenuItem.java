package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.RenderContext;

import javax.swing.Icon;

public class JRadioButtonMenuItem extends javax.swing.JRadioButtonMenuItem {
	public JRadioButtonMenuItem(DimensionProvider dimensionProvider, String name, RenderContext renderContext) {
		super(name);
		dimensionProvider.scaleFont(this, renderContext);
	}

	public JRadioButtonMenuItem(DimensionProvider dimensionProvider, String text, Icon icon, RenderContext renderContext) {
		super(text, icon);
		dimensionProvider.scaleFont(this, renderContext);
	}
}
