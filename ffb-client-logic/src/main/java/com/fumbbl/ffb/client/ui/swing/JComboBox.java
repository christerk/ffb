package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.RenderContext;

public class JComboBox<T> extends javax.swing.JComboBox<T> {
	public JComboBox(DimensionProvider dimensionProvider, T[] items, RenderContext renderContext) {
		super(items);
		dimensionProvider.scaleFont(this, renderContext);
	}

	public JComboBox(DimensionProvider dimensionProvider, RenderContext renderContext) {
		super();
		dimensionProvider.scaleFont(this, renderContext);
	}
}
