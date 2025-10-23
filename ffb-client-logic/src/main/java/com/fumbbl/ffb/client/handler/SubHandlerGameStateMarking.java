package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.marking.FieldMarker;
import com.fumbbl.ffb.marking.PlayerMarker;
import com.fumbbl.ffb.marking.TransientPlayerMarker;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ServerCommandGameState;

import java.util.Arrays;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.CommonPropertyValue;

class SubHandlerGameStateMarking {

	private final FantasyFootballClient client;

	protected SubHandlerGameStateMarking(FantasyFootballClient client) {
		this.client = client;
	}

	public Game handleNetCommand(ServerCommandGameState gameStateCommand) {
		Game existingGame = client.getGame();
		Game incomingGame = gameStateCommand.getGame();

		// Get existing markers
		TransientPlayerMarker[] existingTransientPlayerMarkers = existingGame.getFieldModel().getTransientPlayerMarkers();
		PlayerMarker[] existingPlayerMarkers = existingGame.getFieldModel().getPlayerMarkers();
		FieldMarker[] existingTransientFieldMarkers = existingGame.getFieldModel().getTransientFieldMarkers();
		FieldMarker[] existingFieldMarkers = existingGame.getFieldModel().getFieldMarkers();

		// Set new game
		client.setGame(incomingGame);
		FieldModel fieldModel = incomingGame.getFieldModel();

		// Always keep existing transient markers
		Arrays.stream(existingTransientPlayerMarkers).forEach(fieldModel::addTransient);
		Arrays.stream(existingTransientFieldMarkers).forEach(fieldModel::addTransient);

		boolean reconnecting = incomingGame.getStarted() != null;
		boolean isInitialState = !reconnecting && existingGame.getId() == 0;
		boolean isReplay = client.getMode() == ClientMode.REPLAY;
		boolean isManualMarking = CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_MANUAL
			.equals(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE));

		if (isInitialState || isReplay) {
			Arrays.stream(fieldModel.getFieldMarkers()).forEach(fieldModel::remove);
			Arrays.stream(existingFieldMarkers).forEach(fieldModel::add);
		}

		if (client.getMode() != ClientMode.PLAYER || isInitialState || (!reconnecting && !isManualMarking)) {
			Arrays.stream(fieldModel.getPlayerMarkers()).forEach(fieldModel::remove);
			Arrays.stream(existingPlayerMarkers).forEach(fieldModel::add);
		}

		return incomingGame;
	}
}
