package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;

public class JComboBox<T> extends javax.swing.JComboBox<T> {
	public JComboBox(DimensionProvider dimensionProvider, T[] items) {
		super(items);
		dimensionProvider.scaleFont(this);
	}

	public JComboBox(DimensionProvider dimensionProvider) {
		super();
		dimensionProvider.scaleFont(this);
	}

	@Override
	public T getSelectedItem() {
		//noinspection unchecked
		return (T) super.getSelectedItem();
	}
}
