package com.fumbbl.ffb.client.ui.menu;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.ui.swing.JMenu;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.CardType;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;

import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InducementsMenu extends FfbMenu {

	private int fCurrentInducementTotalHome;
	private int fCurrentUsedCardsHome;
	private int fCurrentInducementTotalAway;
	private int fCurrentUsedCardsAway;

	protected InducementsMenu(FantasyFootballClient client, DimensionProvider dimensionProvider, StyleProvider styleProvider, LayoutSettings layoutSettings) {
		super("Inducements", client, dimensionProvider, styleProvider, layoutSettings);
		setMnemonic(KeyEvent.VK_I);
		setEnabled(false);
	}

	@Override
	public void init() {
		fCurrentInducementTotalHome = -1;
		fCurrentUsedCardsHome = 0;
		fCurrentInducementTotalAway = -1;
		fCurrentUsedCardsAway = 0;
	}

	@Override
	public boolean refresh() {
		boolean refreshNecessary = false;
		Game game = client.getGame();

		InducementSet inducementSetHome = game.getTurnDataHome().getInducementSet();
		int totalInducementHome = inducementSetHome.totalInducements();
		if ((fCurrentInducementTotalHome < 0) || (fCurrentInducementTotalHome != totalInducementHome)) {
			fCurrentInducementTotalHome = totalInducementHome;
			refreshNecessary = true;
		}
		int usedCardsHome = inducementSetHome.getDeactivatedCards().length + inducementSetHome.getActiveCards().length;
		if (usedCardsHome != fCurrentUsedCardsHome) {
			fCurrentUsedCardsHome = usedCardsHome;
			refreshNecessary = true;
		}

		InducementSet inducementSetAway = game.getTurnDataAway().getInducementSet();
		int totalInducementAway = inducementSetAway.totalInducements();
		if ((fCurrentInducementTotalAway < 0) || (fCurrentInducementTotalAway != totalInducementAway)) {
			fCurrentInducementTotalAway = totalInducementAway;
			refreshNecessary = true;
		}
		int usedCardsAway = inducementSetAway.getDeactivatedCards().length + inducementSetAway.getActiveCards().length;
		if (usedCardsAway != fCurrentUsedCardsAway) {
			fCurrentUsedCardsAway = usedCardsAway;
			refreshNecessary = true;
		}

		if (refreshNecessary) {

			JMenu fInducementsHomeMenu;
			JMenu fInducementsAwayMenu;
			removeAll();

			if (fCurrentInducementTotalHome > 0) {
				fInducementsHomeMenu = new JMenu(dimensionProvider, totalInducementHome + " Home Team");
				fInducementsHomeMenu.setForeground(Color.RED);
				fInducementsHomeMenu.setMnemonic(KeyEvent.VK_H);
				add(fInducementsHomeMenu);
				addInducements(fInducementsHomeMenu, inducementSetHome);
			}

			if (fCurrentInducementTotalAway > 0) {
				fInducementsAwayMenu = new JMenu(dimensionProvider, totalInducementAway + " Away Team");
				fInducementsAwayMenu.setForeground(Color.BLUE);
				fInducementsAwayMenu.setMnemonic(KeyEvent.VK_A);
				add(fInducementsAwayMenu);
				addInducements(fInducementsAwayMenu, inducementSetAway);
			}

			if ((fCurrentInducementTotalHome + fCurrentInducementTotalAway) > 0) {
				StringBuilder menuText = new StringBuilder().append(fCurrentInducementTotalHome + fCurrentInducementTotalAway);
				if ((fCurrentInducementTotalHome + fCurrentInducementTotalAway) > 1) {
					menuText.append(" Inducements");
				} else {
					menuText.append(" Inducement");
				}
				setText(menuText.toString());
				setEnabled(true);
			} else {
				setText("No Inducements");
				setEnabled(false);
			}

		}

		return false;
	}

	private void addInducements(JMenu pInducementMenu, InducementSet pInducementSet) {
		Inducement[] inducements = pInducementSet.getInducements();
		Arrays.sort(inducements, Comparator.comparing(pInducement -> pInducement.getType().getName()));
		for (Inducement inducement : inducements) {
			if (!Usage.EXCLUDE_FROM_RESULT.containsAll(inducement.getType().getUsages())) {
				if (inducement.getValue() > 0) {
					StringBuilder inducementText = new StringBuilder();
					inducementText.append(inducement.getValue()).append(" ");
					if (inducement.getValue() > 1) {
						inducementText.append(inducement.getType().getPlural());
					} else {
						inducementText.append(inducement.getType().getSingular());
					}
					JMenuItem inducementItem = new JMenuItem(dimensionProvider, inducementText.toString());
					pInducementMenu.add(inducementItem);
				}
			}
		}

		Game game = client.getGame();
		Team team = pInducementSet.getTurnData().isHomeData() ? game.getTeamHome() : game.getTeamAway();
		List<Player<?>> starPlayers = new ArrayList<>();
		for (Player<?> player : team.getPlayers()) {
			if (player.getPlayerType() == PlayerType.STAR) {
				starPlayers.add(player);
			}
		}
		if (!starPlayers.isEmpty()) {
			StringBuilder starPlayerMenuText = new StringBuilder();
			starPlayerMenuText.append(starPlayers.size());
			if (starPlayers.size() == 1) {
				starPlayerMenuText.append(" Star Player");
			} else {
				starPlayerMenuText.append(" Star Players");
			}
			JMenu starPlayerMenu = new JMenu(dimensionProvider, starPlayerMenuText.toString());
			pInducementMenu.add(starPlayerMenu);
			for (Player<?> player : starPlayers) {
				addPlayerMenuItem(starPlayerMenu, player, player.getName());
			}
		}

		List<Player<?>> mercenaries = new ArrayList<>();
		for (Player<?> player : team.getPlayers()) {
			if (player.getPlayerType() == PlayerType.MERCENARY) {
				mercenaries.add(player);
			}
		}
		if (!mercenaries.isEmpty()) {
			StringBuilder mercenaryMenuText = new StringBuilder();
			mercenaryMenuText.append(mercenaries.size());
			if (mercenaries.size() == 1) {
				mercenaryMenuText.append(" Mercenary");
			} else {
				mercenaryMenuText.append(" Mercenaries");
			}
			JMenu mercenaryMenu = new JMenu(dimensionProvider, mercenaryMenuText.toString());
			pInducementMenu.add(mercenaryMenu);
			for (Player<?> player : mercenaries) {
				addPlayerMenuItem(mercenaryMenu, player, player.getName());
			}
		}

		List<Player<?>> staff = new ArrayList<>();
		for (Player<?> player : team.getPlayers()) {
			if (player.getPlayerType() == PlayerType.INFAMOUS_STAFF) {
				staff.add(player);
			}
		}
		if (!staff.isEmpty()) {
			String staffText = staff.size() + " Infamous Staff";
			JMenu staffMenu = new JMenu(dimensionProvider, staffText);
			pInducementMenu.add(staffMenu);
			for (Player<?> player : staff) {
				addPlayerMenuItem(staffMenu, player, player.getName());
			}
		}

		UserInterface userInterface = client.getUserInterface();
		Map<CardType, List<Card>> cardMap = buildCardMap(pInducementSet);
		for (CardType type : cardMap.keySet()) {
			List<Card> cardList = cardMap.get(type);
			StringBuilder cardTypeText = new StringBuilder();
			cardTypeText.append(cardList.size()).append(" ");
			if (cardList.size() > 1) {
				cardTypeText.append(type.getInducementNameMultiple());
			} else {
				cardTypeText.append(type.getInducementNameSingle());
			}
			int available = 0;
			for (Card card : cardList) {
				if (pInducementSet.isAvailable(card)) {
					available++;
				}
			}
			cardTypeText.append(" (");
			cardTypeText.append((available > 0) ? available : "None");
			cardTypeText.append(" available)");
			if (pInducementSet.getTurnData().isHomeData() && (client.getMode() == ClientMode.PLAYER)) {
				JMenu cardMenu = new JMenu(dimensionProvider, cardTypeText.toString());
				pInducementMenu.add(cardMenu);
				ImageIcon cardIcon = new ImageIcon(
					userInterface.getIconCache().getIconByProperty(IIconProperty.SIDEBAR_OVERLAY_PLAYER_CARD, dimensionProvider));
				for (Card card : cardList) {
					if (pInducementSet.isAvailable(card)) {
						String cardText = "<html>" +
							"<b>" + card.getName() + "</b>" +
							"<br>" + card.getHtmlDescriptionWithPhases() +
							"</html>";
						JMenuItem cardItem = new JMenuItem(dimensionProvider, cardText, cardIcon);
						cardMenu.add(cardItem);
					}
				}
			} else {
				JMenuItem cardItem = new JMenuItem(dimensionProvider, cardTypeText.toString());
				pInducementMenu.add(cardItem);
			}
		}

	}

	private Map<CardType, List<Card>> buildCardMap(InducementSet pInducementSet) {
		Card[] allCards = pInducementSet.getAllCards();

		return Arrays.stream(allCards).collect(Collectors.groupingBy(Card::getType));
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}
}
