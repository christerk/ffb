package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;

public class JComboBox<T> extends javax.swing.JComboBox<T> {
	public JComboBox(DimensionProvider dimensionProvider, T[] items) {
		super(items);
		dimensionProvider.scaleFont(this);
	}
}
