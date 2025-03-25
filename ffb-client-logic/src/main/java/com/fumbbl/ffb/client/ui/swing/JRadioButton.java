package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;

import javax.swing.*;

public class JRadioButton extends javax.swing.JRadioButton {
	public JRadioButton(DimensionProvider dimensionProvider, String name) {
		super(name);
		dimensionProvider.scaleFont(this);
	}

	public JRadioButton(DimensionProvider dimensionProvider, String text, Icon icon) {
		super(text, icon);
		dimensionProvider.scaleFont(this);
	}
}
