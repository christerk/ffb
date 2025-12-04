package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;

public class JTabbedPane extends javax.swing.JTabbedPane {
	public JTabbedPane(DimensionProvider dimensionProvider) {
		super();
		dimensionProvider.scaleFont(this);
	}
}
