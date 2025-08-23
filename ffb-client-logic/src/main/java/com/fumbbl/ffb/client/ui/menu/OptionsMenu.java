package com.fumbbl.ffb.client.ui.menu;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.IGameOption;
import com.fumbbl.ffb.util.StringTool;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Comparator;

public class OptionsMenu extends FfbMenu {
	protected OptionsMenu(FantasyFootballClient client, DimensionProvider dimensionProvider, StyleProvider styleProvider, LayoutSettings layoutSettings) {
		super("Game Options", client, dimensionProvider, styleProvider, layoutSettings);
		setMnemonic(KeyEvent.VK_O);
		setEnabled(false);
	}

	@Override
	protected void init() {

	}

	@Override
	public void refresh() {
		removeAll();
		IGameOption[] gameOptions = client.getGame().getOptions().getOptions();
		Arrays.sort(gameOptions, Comparator.comparing(pO -> pO.getId().getName()));
		int optionsAdded = 0;
		if (client.getGame().isTesting()) {
			JMenuItem optionItem = new JMenuItem(dimensionProvider,
				"* Game is in TEST mode. No results will be uploaded. See help for available test commands.");
			add(optionItem);
			optionsAdded++;
		}
		for (IGameOption option : gameOptions) {
			if (option.isChanged() && (option.getId() != GameOptionId.TEST_MODE)
				&& StringTool.isProvided(option.getDisplayMessage())) {
				JMenuItem optionItem = new JMenuItem(dimensionProvider, "* " + option.getDisplayMessage());
				add(optionItem);
				optionsAdded++;
			}
		}
		if (optionsAdded > 0) {
			StringBuilder menuText = new StringBuilder().append(optionsAdded);
			if (optionsAdded > 1) {
				menuText.append(" Game Options");
			} else {
				menuText.append(" Game Option");
			}
			setText(menuText.toString());
			setEnabled(true);
		} else {
			setText("No Game Options");
			setEnabled(false);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}
}
