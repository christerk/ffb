package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.RenderContext;

public class JPasswordField extends javax.swing.JPasswordField {
	public JPasswordField(DimensionProvider dimensionProvider, int columns) {
		super(columns);
		dimensionProvider.scaleFont(this, RenderContext.UI);
	}
}
