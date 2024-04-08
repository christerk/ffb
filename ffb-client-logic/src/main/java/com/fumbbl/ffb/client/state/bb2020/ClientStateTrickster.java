package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.ClientState;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.ArrayList;
import java.util.List;

public class ClientStateTrickster extends ClientState {
	public ClientStateTrickster(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.TRICKSTER;
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
		if (player == game.getDefender()) {
			createAndShowPopupMenuPlayer();
		}
	}

	protected void createAndShowPopupMenuPlayer() {

		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		IconCache iconCache = userInterface.getIconCache();
		userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
		List<JMenuItem> menuItemList = new ArrayList<>();
		ActingPlayer actingPlayer = game.getActingPlayer();

		addEndActionLabel(iconCache, menuItemList);

		createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		showPopupMenuForPlayer(actingPlayer.getPlayer());

	}

	@Override
	protected boolean mouseOverPlayer(Player<?> player) {
		UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_INVALID_TRICKSTER);
		return true;
	}

	@Override
	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		if (getClient().getGame().getFieldModel().getMoveSquare(pCoordinate) != null) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_TRICKSTER);
		} else {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_INVALID_TRICKSTER);
		}

		return true;
	}

	protected void menuItemSelected(Player<?> player, int pMenuKey) {
		if (pMenuKey == IPlayerPopupMenuKeys.KEY_END_MOVE) {
			getClient().getCommunication().sendEndTurn(TurnMode.TRICKSTER);
		}
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		if (pActionKey == null) {
			return false;
		}
		Player<?> player = getClient().getGame().getActingPlayer().getPlayer();
		if (pActionKey == ActionKey.PLAYER_ACTION_END_MOVE) {
			menuItemSelected(player, IPlayerPopupMenuKeys.KEY_END_MOVE);
			return true;
		}
		return false;
	}

}
