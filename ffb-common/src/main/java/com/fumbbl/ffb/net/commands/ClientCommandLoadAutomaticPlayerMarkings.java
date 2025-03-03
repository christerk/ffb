package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.NetCommandId;

import java.util.List;

public class ClientCommandLoadAutomaticPlayerMarkings extends ClientCommand {
	
	private final List<Game> games;

	public ClientCommandLoadAutomaticPlayerMarkings(List<Game> games) {
		this.games = games;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_LOAD_AUTOMATIC_PLAYER_MARKINGS;
	}

	public List<Game> getGames() {
		return games;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonValue = super.toJsonValue();
		JsonArray jsonGames = new JsonArray();
		games.stream().map(Game::toJsonValue).forEach(jsonGames::add);
		IJsonOption.GAMES.addTo(jsonValue, jsonGames);
		return jsonValue;
	}

	@Override
	public ClientCommand initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		JsonArray jsonGames = IJsonOption.GAMES.getFrom(source, jsonObject);
		jsonGames.values().stream().map(json -> {
			Game game = new Game(source.forContext(FactoryType.FactoryContext.APPLICATION), source.getFactoryManager());
			game.initFrom(source, json);
			return game;
		}).forEach(games::add);

		return this;
	}
}
