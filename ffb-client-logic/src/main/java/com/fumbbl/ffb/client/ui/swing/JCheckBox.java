package com.fumbbl.ffb.client.ui.swing;


import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.RenderContext;

public class JCheckBox extends javax.swing.JCheckBox {
	public JCheckBox(DimensionProvider dimensionProvider, String text, RenderContext renderContext) {
		super(text);
		dimensionProvider.scaleFont(this, renderContext);
	}

	public JCheckBox(DimensionProvider dimensionProvider, RenderContext renderContext) {
		super();
		dimensionProvider.scaleFont(this, renderContext);
	}
}
