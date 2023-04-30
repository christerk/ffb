package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.client.DimensionProvider;


public class JMenu extends javax.swing.JMenu {

	public JMenu(DimensionProvider dimensionProvider, CommonProperty name) {
		this(dimensionProvider, name.getValue());
	}

	public JMenu(DimensionProvider dimensionProvider, String name) {
		super(name);
		dimensionProvider.scaleFont(this);
	}
}
