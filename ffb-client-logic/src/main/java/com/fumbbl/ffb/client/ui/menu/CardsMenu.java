package com.fumbbl.ffb.client.ui.menu;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JMenu;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.ArrayTool;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;

public class CardsMenu extends FfbMenu {

	private Card[] fCurrentActiveCardsHome;
	private Card[] fCurrentActiveCardsAway;

	protected CardsMenu(FantasyFootballClient client, DimensionProvider dimensionProvider) {
		super("Active Cards", client, dimensionProvider);
		setMnemonic(KeyEvent.VK_C);
		setEnabled(false);
	}

	@Override
	protected void init() {
		fCurrentActiveCardsHome = null;
		fCurrentActiveCardsAway = null;

	}

	@Override
	protected void refresh() {
		boolean refreshNecessary = false;

		Game game = client.getGame();

		Card[] cardsHome = game.getTurnDataHome().getInducementSet().getActiveCards();
		if ((fCurrentActiveCardsHome == null) || (cardsHome.length != fCurrentActiveCardsHome.length)) {
			fCurrentActiveCardsHome = cardsHome;
			refreshNecessary = true;
		}

		Card[] cardsAway = game.getTurnDataAway().getInducementSet().getActiveCards();
		if ((fCurrentActiveCardsAway == null) || (cardsAway.length != fCurrentActiveCardsAway.length)) {
			fCurrentActiveCardsAway = cardsAway;
			refreshNecessary = true;
		}

		if (refreshNecessary) {

			removeAll();

			if (ArrayTool.isProvided(fCurrentActiveCardsHome)) {
				JMenu fActiveCardsHomeMenu = new JMenu(dimensionProvider, fCurrentActiveCardsHome.length + " Home Team");
				fActiveCardsHomeMenu.setForeground(Color.RED);
				fActiveCardsHomeMenu.setMnemonic(KeyEvent.VK_H);
				add(fActiveCardsHomeMenu);
				addActiveCards(fActiveCardsHomeMenu, fCurrentActiveCardsHome);
			}

			if (ArrayTool.isProvided(fCurrentActiveCardsAway)) {
				JMenu fActiveCardsAwayMenu = new JMenu(dimensionProvider, fCurrentActiveCardsAway.length + " Away Team");
				fActiveCardsAwayMenu.setForeground(Color.BLUE);
				fActiveCardsAwayMenu.setMnemonic(KeyEvent.VK_A);
				add(fActiveCardsAwayMenu);
				addActiveCards(fActiveCardsAwayMenu, fCurrentActiveCardsAway);
			}

			int currentActiveCardsHomeLength = ArrayTool.isProvided(fCurrentActiveCardsHome) ? fCurrentActiveCardsHome.length
				: 0;
			int currentActiveCardsAwayLength = ArrayTool.isProvided(fCurrentActiveCardsAway) ? fCurrentActiveCardsAway.length
				: 0;

			if ((currentActiveCardsHomeLength + currentActiveCardsAwayLength) > 0) {
				StringBuilder menuText = new StringBuilder()
					.append(currentActiveCardsHomeLength + currentActiveCardsAwayLength);
				if ((currentActiveCardsHomeLength + currentActiveCardsAwayLength) > 1) {
					menuText.append(" Active Cards");
				} else {
					menuText.append(" Active Card");
				}
				setText(menuText.toString());
				setEnabled(true);
			} else {
				setText("No Active Cards");
				setEnabled(false);
			}

		}

	}

	private void addActiveCards(JMenu pCardsMenu, Card[] pCards) {
		Game game = client.getGame();
		Arrays.sort(pCards, Card.createComparator());
		Icon cardIcon = new ImageIcon(
			client.getUserInterface().getIconCache().getIconByProperty(IIconProperty.SIDEBAR_OVERLAY_PLAYER_CARD, dimensionProvider));
		for (Card card : pCards) {
			Player<?> player = null;
			if (card.getTarget().isPlayedOnPlayer()) {
				player = game.getFieldModel().findPlayer(card);
			}
			StringBuilder cardText = new StringBuilder();
			cardText.append("<html>");
			cardText.append("<b>").append(card.getName()).append("</b>");
			if (player != null) {
				cardText.append("<br>").append("Played on ").append(player.getName());
			}
			cardText.append("<br>").append(card.getHtmlDescription());
			cardText.append("</html>");
			if (player != null) {
				addPlayerMenuItem(pCardsMenu, player, cardText.toString());
			} else {
				JMenuItem cardMenuItem = new JMenuItem(dimensionProvider, cardText.toString(), cardIcon);
				pCardsMenu.add(cardMenuItem);
			}
		}
	}


	@Override
	public void actionPerformed(ActionEvent e) {

	}
}
