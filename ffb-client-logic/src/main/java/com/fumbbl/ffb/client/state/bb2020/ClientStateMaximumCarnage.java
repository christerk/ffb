package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.ClientState;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.client.util.UtilClientStateBlocking;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.ArrayList;
import java.util.List;

public class ClientStateMaximumCarnage extends ClientState {
	public ClientStateMaximumCarnage(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.MAXIMUM_CARNAGE;
	}

	@Override
	public void enterState() {
		super.enterState();
	}

	protected void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayer() == pPlayer) {

			createAndShowPopupMenuForBlockingPlayer();

		} else if (!pPlayer.getId().equalsIgnoreCase(game.getLastDefenderId()) && !game.getActingTeam().hasPlayer(pPlayer)) {
			UtilClientStateBlocking.block(this, actingPlayer.getPlayerId(), pPlayer, false, true, false);
		}
	}

	@Override
	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayer() == pPlayer || pPlayer.getId().equalsIgnoreCase(game.getLastDefenderId()) || game.getActingTeam().hasPlayer(pPlayer)) {
			UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		} else {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_BLOCK);

		}
		return super.mouseOverPlayer(pPlayer);
	}

	@Override
	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		return super.mouseOverField(pCoordinate);
	}

	private void createAndShowPopupMenuForBlockingPlayer() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		List<JMenuItem> menuItemList = new ArrayList<>();
		UserInterface userInterface = getClient().getUserInterface();
		IconCache iconCache = userInterface.getIconCache();
		userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
		addEndActionLabel(iconCache, menuItemList);
		createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		showPopupMenuForPlayer(actingPlayer.getPlayer());
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		boolean actionHandled = true;
		if (pActionKey == ActionKey.PLAYER_ACTION_END_MOVE) {
			menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_END_MOVE);
		} else {
			actionHandled = false;
		}
		return actionHandled;
	}

	@Override
	public void endTurn() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_END_MOVE);
		getClient().getCommunication().sendEndTurn(game.getTurnMode());
	}

	protected void menuItemSelected(Player<?> pPlayer, int pMenuKey) {
		if (pPlayer != null) {
			if (pMenuKey == IPlayerPopupMenuKeys.KEY_END_MOVE) {
				getClient().getCommunication().sendActingPlayer(null, null, false);
			}
		}
	}
}
