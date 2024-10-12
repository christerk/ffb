package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.ClientState;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientStateThenIStartedBlastin extends ClientState {

	public ClientStateThenIStartedBlastin(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.THEN_I_STARTED_BLASTIN;
	}

	@Override
	public void enterState() {
		super.enterState();
		Game game = getClient().getGame();
		FieldModel fieldModel = game.getFieldModel();
		Player<?> player = game.playingTeamHasActingPLayer() ? game.getActingPlayer().getPlayer() : game.getDefender();
		MoveSquare[] squares = Arrays.stream(fieldModel.findAdjacentCoordinates(fieldModel.getPlayerCoordinate(player), FieldCoordinateBounds.FIELD,
			3, false)).map(fieldCoordinate -> new MoveSquare(fieldCoordinate, 0, 0)).toArray(MoveSquare[]::new);
		fieldModel.add(squares);
		getClient().getUserInterface().getFieldComponent().refresh();
	}

	@Override
	public void leaveState() {
		FieldModel fieldModel = getClient().getGame().getFieldModel();
		fieldModel.clearMoveSquares();
		getClient().getUserInterface().getFieldComponent().refresh();
	}

	protected void clickOnPlayer(Player<?> player) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			if (game.playingTeamHasActingPLayer()) {
				createAndShowPopupMenuForActingPlayer();
			}
		} else {
			if (isValidTarget(player, game)) {
				getClient().getCommunication().sendTargetSelected(player.getId());
			}
		}
	}


	protected boolean mouseOverPlayer(Player<?> player) {
		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		getClient().getClientData().setSelectedPlayer(player);
		userInterface.refreshSideBars();
		if (isValidTarget(player, game)) {
			UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_BLASTIN);
		} else {
			UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_INVALID_BLASTIN);
		}

		return true;
	}

	private boolean isValidTarget(Player<?> player, Game game) {
		FieldCoordinate sourceCoordinate;
		if (game.playingTeamHasActingPLayer()) {
			ActingPlayer actingPlayer = game.getActingPlayer();
			sourceCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		} else  {
			sourceCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
		}
		FieldCoordinate targetCoordinate = game.getFieldModel().getPlayerCoordinate(player);
		int distance = targetCoordinate.distanceInSteps(sourceCoordinate);

		PlayerState playerState = game.getFieldModel().getPlayerState(player);


		return distance <= 3 && playerState.getBase() == PlayerState.STANDING
			&& (player.getTeam() != game.getActingTeam() || !game.playingTeamHasActingPLayer());
	}

	@Override
	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		UserInterface userInterface = getClient().getUserInterface();
		UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_INVALID_BLASTIN);
		return super.mouseOverField(pCoordinate);
	}

	protected void createAndShowPopupMenuForActingPlayer() {

		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		IconCache iconCache = userInterface.getIconCache();
		userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
		List<JMenuItem> menuItemList = new ArrayList<>();
		ActingPlayer actingPlayer = game.getActingPlayer();

		if (isEndPlayerActionAvailable()) {
			addEndActionLabel(iconCache, menuItemList);
		}

		createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		showPopupMenuForPlayer(actingPlayer.getPlayer());

	}

	protected void menuItemSelected(Player<?> player, int pMenuKey) {
		ClientCommunication communication = getClient().getCommunication();
		switch (pMenuKey) {
			case IPlayerPopupMenuKeys.KEY_END_MOVE:
				if (isEndPlayerActionAvailable()) {
					communication.sendEndTurn(getClient().getGame().getTurnMode());
				}
				break;

			default:
				break;
		}
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		if (pActionKey == null) {
			return false;
		}
		Player<?> player = getClient().getGame().getActingPlayer().getPlayer();
		switch (pActionKey) {
			case PLAYER_ACTION_END_MOVE:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_END_MOVE);
				return true;
			default:
				return super.actionKeyPressed(pActionKey);
		}
	}

	private boolean isEndPlayerActionAvailable() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return !actingPlayer.hasActed();
	}

	@Override
	protected String deselectPlayerLabel() {
		return "Don't blast";
	}
}
