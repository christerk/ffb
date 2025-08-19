package com.fumbbl.ffb.client.ui.menu.settings;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.ui.menu.FfbMenu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ClientSettingsMenu extends FfbMenu {
	protected ClientSettingsMenu(FantasyFootballClient client, DimensionProvider dimensionProvider, StyleProvider styleProvider, LayoutSettings layoutSettings) {
		super("Client Settings", client, dimensionProvider, styleProvider, layoutSettings);
		setMnemonic(KeyEvent.VK_S);
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
