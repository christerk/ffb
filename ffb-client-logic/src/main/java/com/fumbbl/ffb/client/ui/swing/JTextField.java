package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.RenderContext;

public class JTextField extends javax.swing.JTextField {
	public JTextField(DimensionProvider dimensionProvider, String text, RenderContext renderContext) {
		super(text);
		dimensionProvider.scaleFont(this, renderContext);
	}

	public JTextField(DimensionProvider dimensionProvider, int columns, RenderContext renderContext) {
		super(columns);
		dimensionProvider.scaleFont(this, renderContext);
	}

	public JTextField(DimensionProvider dimensionProvider, RenderContext renderContext) {
		super();
		dimensionProvider.scaleFont(this, renderContext);
	}
}
