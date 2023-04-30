package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;

public class JTextField extends javax.swing.JTextField {
	public JTextField(DimensionProvider dimensionProvider, String text) {
		super(text);
		dimensionProvider.scaleFont(this);
	}

	public JTextField(DimensionProvider dimensionProvider, int columns) {
		super(columns);
		dimensionProvider.scaleFont(this);
	}

	public JTextField(DimensionProvider dimensionProvider) {
		super();
		dimensionProvider.scaleFont(this);
	}
}
