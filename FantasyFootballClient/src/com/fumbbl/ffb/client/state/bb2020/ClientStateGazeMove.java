package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.ClientStateMove;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TargetSelectionState;

public class ClientStateGazeMove extends ClientStateMove {
	public ClientStateGazeMove(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.GAZE_MOVE;
	}

	@Override
	protected boolean mouseOverPlayer(Player<?> player) {
		boolean result = super.mouseOverPlayer(player);

		Game game = getClient().getGame();
		FieldModel fieldModel = game.getFieldModel();
		TargetSelectionState targetSelectionState = getClient().getGame().getFieldModel().getTargetSelectionState();
		if (targetSelectionState != null && targetSelectionState.isSelected()) {
			boolean isTargetedPlayer = player.getId().equalsIgnoreCase(targetSelectionState.getSelectedPlayerId());
			ActingPlayer actingPlayer = game.getActingPlayer();
			boolean isAdjacent = fieldModel.getPlayerCoordinate(actingPlayer.getPlayer()).isAdjacent(fieldModel.getPlayerCoordinate(player));

			if (isTargetedPlayer && isAdjacent) {
				UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_GAZE);
			}
		}

		return result;
	}

	@Override
	protected void clickOnPlayer(Player<?> player) {

		Game game = getClient().getGame();
		FieldModel fieldModel = game.getFieldModel();
		TargetSelectionState targetSelectionState = fieldModel.getTargetSelectionState();
		if (targetSelectionState != null && targetSelectionState.isSelected()) {
			boolean isTargetedPlayer = player.getId().equalsIgnoreCase(targetSelectionState.getSelectedPlayerId());
			ActingPlayer actingPlayer = game.getActingPlayer();
			boolean isAdjacent = fieldModel.getPlayerCoordinate(actingPlayer.getPlayer()).isAdjacent(fieldModel.getPlayerCoordinate(player));
			if (isTargetedPlayer && isAdjacent) {
				getClient().getCommunication().sendGaze(actingPlayer.getPlayerId(), player);
				return;
			}
		}

		super.clickOnPlayer(player);
	}
}
