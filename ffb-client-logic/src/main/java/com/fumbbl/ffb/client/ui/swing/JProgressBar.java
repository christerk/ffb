package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.RenderContext;

public class JProgressBar extends javax.swing.JProgressBar {
	public JProgressBar(DimensionProvider dimensionProvider, int min, int max) {
		super(min, max);
		dimensionProvider.scaleFont(this, RenderContext.UI);
	}
}
