package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.client.FontConfig;
import com.fumbbl.ffb.client.FontConfigRegistry;

import javax.swing.Icon;

import java.awt.*;

import static com.fumbbl.ffb.client.FontConfig.Size.MEDIUM;
import static java.awt.Font.PLAIN;

public class JRadioButtonMenuItem extends javax.swing.JRadioButtonMenuItem {
    public JRadioButtonMenuItem(DimensionProvider dimensionProvider,
                                String name,
                                FontCache fontCache,
                                FontConfigRegistry fontConfigRegistry) {
		super(name);
        FontConfig fc = fontConfigRegistry.getConfig(dimensionProvider.getLayoutSettings().getLayout());
        Font font = fontCache.font(PLAIN, fc.getSize(MEDIUM), dimensionProvider);
        setFont(font);
		dimensionProvider.scaleFont(this);
	}

	public JRadioButtonMenuItem(DimensionProvider dimensionProvider,
                                String text,
                                Icon icon,
                                FontCache fontCache,
                                FontConfigRegistry fontConfigRegistry) {
		super(text, icon);
        FontConfig fc = fontConfigRegistry.getConfig(dimensionProvider.getLayoutSettings().getLayout());
        Font font = fontCache.font(PLAIN, fc.getSize(MEDIUM), dimensionProvider);
        setFont(font);
		dimensionProvider.scaleFont(this);
	}
}
