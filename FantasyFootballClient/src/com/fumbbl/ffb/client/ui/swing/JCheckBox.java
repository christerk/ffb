package com.fumbbl.ffb.client.ui.swing;


import com.fumbbl.ffb.client.DimensionProvider;

public class JCheckBox extends javax.swing.JCheckBox {
	public JCheckBox(DimensionProvider dimensionProvider, String text) {
		super(text);
		dimensionProvider.scaleFont(this);
	}

	public JCheckBox(DimensionProvider dimensionProvider) {
		super();
		dimensionProvider.scaleFont(this);
	}
}
