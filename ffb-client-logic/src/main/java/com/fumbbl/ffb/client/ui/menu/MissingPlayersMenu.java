package com.fumbbl.ffb.client.ui.menu;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.PlayerIconFactory;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.ui.BoxComponent;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class MissingPlayersMenu extends FfbMenu {
	protected MissingPlayersMenu(FantasyFootballClient client, DimensionProvider dimensionProvider, StyleProvider styleProvider, LayoutSettings layoutSettings) {
		super("Missing Players", client, dimensionProvider, styleProvider, layoutSettings);
		setMnemonic(KeyEvent.VK_M);
		setEnabled(false);
	}

	@Override
	public void init() {

	}

	@Override
	public boolean refresh() {
		Game game = client.getGame();
		removeAll();
		int nrOfEntries = 0;
		for (int i = 0; i < BoxComponent.MAX_BOX_ELEMENTS; i++) {
			Player<?> player = game.getFieldModel().getPlayer(new FieldCoordinate(FieldCoordinate.MNG_HOME_X, i));
			if (player != null) {
				addMissingPlayerMenuItem(player);
				nrOfEntries++;
			} else {
				break;
			}
		}
		for (int i = 0; i < BoxComponent.MAX_BOX_ELEMENTS; i++) {
			Player<?> player = game.getFieldModel().getPlayer(new FieldCoordinate(FieldCoordinate.MNG_AWAY_X, i));
			if (player != null) {
				addMissingPlayerMenuItem(player);
				nrOfEntries++;
			} else {
				break;
			}
		}
		StringBuilder menuText = new StringBuilder();
		if (nrOfEntries > 0) {
			menuText.append(nrOfEntries);
			if (nrOfEntries > 1) {
				menuText.append(" Missing Players");
			} else {
				menuText.append(" Missing Player");
			}
			setEnabled(true);
		} else {
			menuText.append("No Missing Players");
			setEnabled(false);
		}
		setText(menuText.toString());

		return false;
	}

	private void addMissingPlayerMenuItem(Player<?> pPlayer) {
		if (pPlayer == null) {
			return;
		}
		StringBuilder playerText = new StringBuilder();
		playerText.append("<html>").append(pPlayer.getName());
		if (pPlayer.getRecoveringInjury() != null) {
			playerText.append("<br>").append(pPlayer.getRecoveringInjury().getRecovery());
		} else {
			Game game = client.getGame();
			PlayerResult playerResult = game.getGameResult().getPlayerResult(pPlayer);
			if (playerResult.getSendToBoxReason() != null) {
				playerText.append("<br>").append(playerResult.getSendToBoxReason().getReason());
			}
		}
		playerText.append("</html>");
		addPlayerMenuItem(pPlayer, playerText.toString());
	}

	private void addPlayerMenuItem(Player<?> pPlayer, String pText) {
		if ((pPlayer == null) || !StringTool.isProvided(pText)) {
			return;
		}
		UserInterface userInterface = client.getUserInterface();
		PlayerIconFactory playerIconFactory = userInterface.getPlayerIconFactory();
		Icon playerIcon = new ImageIcon(playerIconFactory.getIcon(client, pPlayer, dimensionProvider));
		JMenuItem playersMenuItem = new JMenuItem(dimensionProvider, pText, playerIcon);
		playersMenuItem.addMouseListener(new FfbMenu.MenuPlayerMouseListener(pPlayer));
		add(playersMenuItem);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}
}
