package com.fumbbl.ffb.client.ui.menu.settings;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.dialog.DialogSelectLocalStoredProperties;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.ui.menu.FfbMenu;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import static com.fumbbl.ffb.CommonProperty.SETTING_LOCAL_SETTINGS;

public class UserSettingsMenu extends FfbMenu {
	private ClientSettingsMenu clientSettingsMenu;
	private ClientGraphicsMenu clientGraphicsMenu;
	private GamePlayMenu gamePlayMenu;
	private JMenuItem localPropertiesItem;

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

		createLocalPropertiesItem();
	}

	@Override
	public void refresh() {
		clientSettingsMenu.refresh();
		clientGraphicsMenu.refresh();
		gamePlayMenu.refresh();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		clientSettingsMenu.actionPerformed(e);
		clientGraphicsMenu.actionPerformed(e);
		gamePlayMenu.actionPerformed(e);

		javax.swing.JMenuItem source = (javax.swing.JMenuItem) (e.getSource());

		if (source == localPropertiesItem) {
			showDialog(new DialogSelectLocalStoredProperties(client));
		}
	}

	private void createLocalPropertiesItem() {
		localPropertiesItem = new JMenuItem(dimensionProvider, SETTING_LOCAL_SETTINGS.getValue());
		localPropertiesItem.setMnemonic(KeyEvent.VK_L);
		localPropertiesItem.addActionListener(this);
		add(localPropertiesItem);
	}

	public void dialogClosed(IDialog pDialog) {
		client.getUserInterface().dialogClosed(pDialog);
		switch (pDialog.getId()) {
			case STORE_PROPERTIES_LOCAL:
				DialogSelectLocalStoredProperties dialogSelectLocalStoredProperties = (DialogSelectLocalStoredProperties) pDialog;
				if (dialogSelectLocalStoredProperties.getSelectedProperties() != null) {
					client.setLocallyStoredPropertyKeys(dialogSelectLocalStoredProperties.getSelectedProperties());
					client.saveUserSettings(false);
				}
				break;
			default:
				break;
		}
	}
}
