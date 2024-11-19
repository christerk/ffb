package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.RenderContext;


public class JMenu extends javax.swing.JMenu {

	public JMenu(DimensionProvider dimensionProvider, CommonProperty name, RenderContext renderContext) {
		this(dimensionProvider, name.getValue(), renderContext);
	}

	public JMenu(DimensionProvider dimensionProvider, String name, RenderContext renderContext) {
		super(name);
		dimensionProvider.scaleFont(this, renderContext);
	}
}
