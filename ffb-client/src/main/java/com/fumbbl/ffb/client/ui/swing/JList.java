package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;

import javax.swing.ListModel;

public class JList<T> extends javax.swing.JList<T> {

	public JList(DimensionProvider dimensionProvider, ListModel<T> dataModel) {
		super(dataModel);
		dimensionProvider.scaleFont(this);
	}
}
