package com.fumbbl.ffb.client.ui.menu.settings;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.ui.menu.FfbMenu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class UserSettingsMenu extends FfbMenu {
	private ClientSettingsMenu clientSettingsMenu;
	private ClientGraphicsMenu clientGraphicsMenu;
	private GamePlayMenu gamePlayMenu;

	public UserSettingsMenu(FantasyFootballClient client, DimensionProvider dimensionProvider, StyleProvider styleProvider, LayoutSettings layoutSettings) {
		super("User Settings", client, dimensionProvider, styleProvider, layoutSettings);
		setMnemonic(KeyEvent.VK_U);
	}

	@Override
	protected void init() {
		clientGraphicsMenu = new ClientGraphicsMenu(client, dimensionProvider, styleProvider, layoutSettings);
		add(clientGraphicsMenu);

		clientSettingsMenu = new ClientSettingsMenu(client, dimensionProvider, styleProvider, layoutSettings);
		add(clientSettingsMenu);

		gamePlayMenu = new GamePlayMenu(client, dimensionProvider, styleProvider, layoutSettings);
		add(gamePlayMenu);
	}

	@Override
	protected void refresh() {
		clientSettingsMenu.refresh();
		clientGraphicsMenu.refresh();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		clientSettingsMenu.actionPerformed(e);
		clientGraphicsMenu.actionPerformed(e);
	}
}
