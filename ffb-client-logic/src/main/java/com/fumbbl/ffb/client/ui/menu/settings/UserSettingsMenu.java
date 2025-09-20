package com.fumbbl.ffb.client.ui.menu.settings;

import com.fumbbl.ffb.CommonProperty;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.fumbbl.ffb.CommonProperty.SETTING_LOCAL_SETTINGS;

public class UserSettingsMenu extends FfbMenu {
	private GamePlayMenu gamePlayMenu;
	private JMenuItem localPropertiesItem;
	private Set<FfbMenu> subMenus;

	public UserSettingsMenu(FantasyFootballClient client, DimensionProvider dimensionProvider, StyleProvider styleProvider, LayoutSettings layoutSettings) {
		super("User Settings", client, dimensionProvider, styleProvider, layoutSettings);
		setMnemonic(KeyEvent.VK_U);
	}

	@Override
	public javax.swing.JMenuItem add(javax.swing.JMenuItem menuItem) {
		if (menuItem instanceof FfbMenu) {
			subMenus.add((FfbMenu) menuItem);
		}
		return super.add(menuItem);
	}

	@Override
	public void init() {
		subMenus = new HashSet<>();

		ClientGraphicsMenu clientGraphicsMenu = new ClientGraphicsMenu(client, dimensionProvider, styleProvider, layoutSettings);
		add(clientGraphicsMenu);

		ClientSettingsMenu clientSettingsMenu = new ClientSettingsMenu(client, dimensionProvider, styleProvider, layoutSettings);
		add(clientSettingsMenu);

		gamePlayMenu = new GamePlayMenu(client, dimensionProvider, styleProvider, layoutSettings);
		add(gamePlayMenu);

		createLocalPropertiesItem();

		subMenus.forEach(FfbMenu::init);
	}

	@Override
	public boolean refresh() {
		return subMenus.stream().map(FfbMenu::refresh).reduce((a, b) -> a || b).orElse(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		subMenus.forEach(menu -> menu.actionPerformed(e));

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

	public String menuName(CommonProperty menuProperty) {
		return gamePlayMenu.menuName(menuProperty);
	}

	public Map<String, String> menuEntries(CommonProperty menuProperty) {
		return gamePlayMenu.menuEntries(menuProperty);
	}
}
