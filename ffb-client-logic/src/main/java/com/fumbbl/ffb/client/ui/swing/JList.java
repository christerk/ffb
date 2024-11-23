package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.RenderContext;

import javax.swing.ListModel;

public class JList<T> extends javax.swing.JList<T> {

	public JList(DimensionProvider dimensionProvider, ListModel<T> dataModel, RenderContext renderContext) {
		super(dataModel);
		dimensionProvider.scaleFont(this, renderContext);
	}
}
