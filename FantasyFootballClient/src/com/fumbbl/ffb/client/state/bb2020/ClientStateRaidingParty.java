package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.ClientState;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.util.ArrayList;
import java.util.List;

public class ClientStateRaidingParty extends ClientState {
	public ClientStateRaidingParty(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.RAIDING_PARTY;
	}

	@Override
	protected void clickOnField(FieldCoordinate pCoordinate) {
		if (getClient().getGame().getFieldModel().getMoveSquare(pCoordinate) != null) {
			getClient().getCommunication().sendFieldCoordinate(pCoordinate);
		}
	}

	@Override
	protected void clickOnPlayer(Player<?> player) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			createAndShowPopupMenuForActingPlayer();
		}
	}

	protected void createAndShowPopupMenuForActingPlayer() {

		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		IconCache iconCache = userInterface.getIconCache();
		userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
		List<JMenuItem> menuItemList = new ArrayList<>();
		ActingPlayer actingPlayer = game.getActingPlayer();

		menuItemList.add(createCancelRaidingPartyItem(iconCache));

		createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		showPopupMenuForPlayer(actingPlayer.getPlayer());

	}

	@Override
	protected boolean mouseOverPlayer(Player<?> player) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		} else {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_INVALID_RAID);
		}
		return true;
	}

	@Override
	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		if (getClient().getGame().getFieldModel().getMoveSquare(pCoordinate) != null) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_RAID);
		} else {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_INVALID_RAID);
		}

		return true;
	}

	protected JMenuItem createCancelRaidingPartyItem(IconCache iconCache) {
		JMenuItem menuItem = new JMenuItem("Cancel Raiding Party",
			new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_RAIDING_PARTY)));
		menuItem.setMnemonic(IPlayerPopupMenuKeys.KEY_RAIDING_PARTY);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_RAIDING_PARTY, 0));
		return menuItem;
	}

	protected void menuItemSelected(Player<?> player, int pMenuKey) {
		if (pMenuKey == IPlayerPopupMenuKeys.KEY_RAIDING_PARTY) {
			getClient().getCommunication().sendPlayerChoice(PlayerChoiceMode.RAIDING_PARTY, null);
		}
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		if (pActionKey == null) {
			return false;
		}
		Player<?> player = getClient().getGame().getActingPlayer().getPlayer();
		if (pActionKey == ActionKey.PLAYER_ACTION_RAIDING_PARTY) {
			menuItemSelected(player, IPlayerPopupMenuKeys.KEY_RAIDING_PARTY);
			return true;
		}
		return false;
	}

}
