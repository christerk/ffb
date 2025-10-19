package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.marking.FieldMarker;
import com.fumbbl.ffb.marking.PlayerMarker;
import com.fumbbl.ffb.marking.TransientPlayerMarker;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ServerCommandGameState;

import java.util.Arrays;

class SubHandlerGameStateMarking {
	
	private final FantasyFootballClient client;

	protected SubHandlerGameStateMarking(FantasyFootballClient client) {
		this.client = client;
	}

	public Game handleNetCommand(ServerCommandGameState gameStateCommand) {
		TransientPlayerMarker[] transientPlayerMarkers = client.getGame().getFieldModel().getTransientPlayerMarkers();
		PlayerMarker[] playerMarkers = client.getGame().getFieldModel().getPlayerMarkers();
		FieldMarker[] transientFieldMarkers = client.getGame().getFieldModel().getTransientFieldMarkers();
		FieldMarker[] fieldMarkers = client.getGame().getFieldModel().getFieldMarkers();

		Game game = gameStateCommand.getGame();
		client.setGame(game);
		boolean firstGameState = client.getGame().getId() > 0;
		if (firstGameState) {

			FieldModel fieldModel = game.getFieldModel();

			Arrays.stream(transientPlayerMarkers).forEach(fieldModel::addTransient);
			Arrays.stream(transientFieldMarkers).forEach(fieldModel::addTransient);
			Arrays.stream(fieldModel.getFieldMarkers()).forEach(fieldModel::remove);
			Arrays.stream(fieldModel.getPlayerMarkers()).forEach(fieldModel::remove);

			Arrays.stream(playerMarkers).forEach(fieldModel::add);
			Arrays.stream(fieldMarkers).forEach(fieldModel::add);
		}
		return game;
	}
}
