package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;

public class JPasswordField extends javax.swing.JPasswordField {
	public JPasswordField(DimensionProvider dimensionProvider, int columns) {
		super(columns);
		dimensionProvider.scaleFont(this);
	}
}
