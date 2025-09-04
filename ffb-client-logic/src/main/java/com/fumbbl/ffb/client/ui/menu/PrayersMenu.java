package com.fumbbl.ffb.client.ui.menu;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.ui.swing.JMenu;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.factory.bb2020.PrayerFactory;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.Game;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PrayersMenu extends FfbMenu {
	private final List<Prayer> currentPrayersHome = new ArrayList<>();
	private final List<Prayer> currentPrayersAway = new ArrayList<>();

	protected PrayersMenu(FantasyFootballClient client, DimensionProvider dimensionProvider, StyleProvider styleProvider, LayoutSettings layoutSettings) {
		super("Prayers", client, dimensionProvider, styleProvider, layoutSettings);
		setMnemonic(KeyEvent.VK_P);
		setEnabled(false);
	}

	@Override
	protected void init() {

	}

	@Override
	public boolean refresh() {
		boolean refreshNecessary = false;
		Game game = client.getGame();

		Set<Prayer> prayersHome = game.getTurnDataHome().getInducementSet().getPrayers();
		if (prayersHome.size() != currentPrayersHome.size()) {
			PrayerFactory prayerFactory = game.getFactory(FactoryType.Factory.PRAYER);
			currentPrayersHome.clear();
			currentPrayersHome.addAll(prayerFactory.sort(prayersHome));
			refreshNecessary = true;
		}

		Set<Prayer> prayersAway = game.getTurnDataAway().getInducementSet().getPrayers();
		if (prayersAway.size() != currentPrayersAway.size()) {
			PrayerFactory prayerFactory = game.getFactory(FactoryType.Factory.PRAYER);
			currentPrayersAway.clear();
			currentPrayersAway.addAll(prayerFactory.sort(prayersAway));
			refreshNecessary = true;
		}

		if (refreshNecessary) {

			removeAll();

			if (!currentPrayersHome.isEmpty()) {
				JMenu prayersHomeMenu = new JMenu(dimensionProvider, currentPrayersHome.size() + " Home Team");
				prayersHomeMenu.setForeground(Color.RED);
				prayersHomeMenu.setMnemonic(KeyEvent.VK_H);
				add(prayersHomeMenu);
				addPrayers(prayersHomeMenu, currentPrayersHome);
			}

			if (!currentPrayersAway.isEmpty()) {
				JMenu prayersAwayMenu = new JMenu(dimensionProvider, currentPrayersAway.size() + " Away Team");
				prayersAwayMenu.setForeground(Color.BLUE);
				prayersAwayMenu.setMnemonic(KeyEvent.VK_A);
				add(prayersAwayMenu);
				addPrayers(prayersAwayMenu, currentPrayersAway);
			}

			int totalPrayers = currentPrayersHome.size() + currentPrayersAway.size();
			if (totalPrayers > 0) {
				StringBuilder menuText = new StringBuilder()
					.append(totalPrayers);
				if (totalPrayers > 1) {
					menuText.append(" Prayers");
				} else {
					menuText.append(" Prayer");
				}
				setText(menuText.toString());
				setEnabled(true);
			} else {
				setText("No Prayers");
				setEnabled(false);
			}
		}
		return false;
	}

	private void addPrayers(JMenu prayerMenu, List<Prayer> prayers) {
		for (Prayer prayer : prayers) {
			String text = "<html>" +
				"<b>" + prayer.getName() + "</b>" +
				"<br>" + prayer.getDuration().getDescription() + ": " + prayer.getDescription() +
				"</html>";
			JMenuItem menuItem = new JMenuItem(dimensionProvider, text);
			prayerMenu.add(menuItem);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}
}
