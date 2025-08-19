package com.fumbbl.ffb.client.ui.menu.settings;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.ui.menu.FfbMenu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ClientGraphicsMenu extends FfbMenu {
	protected ClientGraphicsMenu(FantasyFootballClient client, DimensionProvider dimensionProvider, StyleProvider styleProvider, LayoutSettings layoutSettings) {
		super("Client Graphics", client, dimensionProvider, styleProvider, layoutSettings);
		setMnemonic(KeyEvent.VK_C);
	}

	@Override
	protected void init() {

	}

	@Override
	protected void refresh() {

	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}
}
