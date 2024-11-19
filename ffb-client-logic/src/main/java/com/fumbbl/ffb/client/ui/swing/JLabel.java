package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.RenderContext;

import javax.swing.Icon;

public class JLabel extends javax.swing.JLabel {
	public JLabel(DimensionProvider dimensionProvider, String text, RenderContext renderContext) {
		super(text);
		dimensionProvider.scaleFont(this, renderContext);
	}

	public JLabel(DimensionProvider dimensionProvider, RenderContext renderContext) {
		super();
		dimensionProvider.scaleFont(this, renderContext);
	}

	public JLabel(DimensionProvider dimensionProvider, Icon image, RenderContext renderContext) {
		super(image);
		dimensionProvider.scaleFont(this, renderContext);
	}
}
