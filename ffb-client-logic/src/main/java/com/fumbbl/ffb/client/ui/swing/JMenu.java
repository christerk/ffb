package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.client.DimensionProvider;

import javax.swing.*;


public class JMenu extends javax.swing.JMenu {

	public JMenu(DimensionProvider dimensionProvider, CommonProperty name) {
		this(dimensionProvider, name.getValue());
	}

	public JMenu(DimensionProvider dimensionProvider, String name) {
		super(name);
		dimensionProvider.scaleFont(this);
	}

	public JMenu(DimensionProvider dimensionProvider, String name, ImageIcon icon) {
		super(name);
		if (icon != null) {
			setIcon(dimensionProvider.scaleIcon(icon));
		}
		dimensionProvider.scaleFont(this);
	}
}
