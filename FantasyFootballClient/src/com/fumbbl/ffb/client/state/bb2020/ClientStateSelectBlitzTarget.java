package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.client.state.ClientStateMove;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.UtilPlayer;

public class ClientStateSelectBlitzTarget extends ClientStateMove {

	public ClientStateSelectBlitzTarget(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.SELECT_BLITZ_TARGET;
	}

	public void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (pPlayer.equals(actingPlayer.getPlayer()) || (!actingPlayer.hasBlocked() && UtilPlayer.isValidBlitzTarget(game, pPlayer))) {
			getClient().getCommunication().sendTargetSelected(pPlayer.getId());
		}
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		Game game = getClient().getGame();
		FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
		fieldComponent.getLayerUnderPlayers().clearMovePath();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (!actingPlayer.hasBlocked() && UtilPlayer.isValidBlitzTarget(game, pPlayer)) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_BLOCK);
		} else {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_INVALID_BLOCK);
		}

		showShortestPath(game.getFieldModel().getPlayerCoordinate(pPlayer), game, fieldComponent, actingPlayer);

		return true;
	}

	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		super.mouseOverField(pCoordinate);
		Game game = getClient().getGame();
		FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
		fieldComponent.getLayerUnderPlayers().clearMovePath();
		ActingPlayer actingPlayer = game.getActingPlayer();

		UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_INVALID_BLOCK);

		showShortestPath(pCoordinate, game, fieldComponent, actingPlayer);

		return true;
	}

	@Override
	protected void clickOnField(FieldCoordinate pCoordinate) {
		// clicks on fields are ignored
	}
}
