package com.fumbbl.ffb.server.net.commands;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.marking.AutoMarkingConfig;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.server.IServerJsonOption;

import java.util.List;

public class InternalServerCommandCalculateAutomaticPlayerMarkings extends InternalServerCommand {

	private AutoMarkingConfig autoMarkingConfig;
	private final List<Game> games;

	public InternalServerCommandCalculateAutomaticPlayerMarkings(AutoMarkingConfig autoMarkingConfig, List<Game> games) {
		super();
		this.autoMarkingConfig = autoMarkingConfig;
		this.games = games;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.INTERNAL_CALCULATE_AUTOMATIC_PLAYER_MARKINGS;
	}

	public AutoMarkingConfig getAutoMarkingConfig() {
		return autoMarkingConfig;
	}

	public List<Game> getGames() {
		return games;
	}

// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.AUTO_MARKING_CONFIG.addTo(jsonObject, UtilJson.toJsonObject(autoMarkingConfig.toJsonValue()));
		JsonArray jsonGames = new JsonArray();
		games.stream().map(Game::toJsonValue).forEach(jsonGames::add);
		IJsonOption.GAMES.addTo(jsonObject, jsonGames);
		return jsonObject;
	}

	public InternalServerCommandCalculateAutomaticPlayerMarkings initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		autoMarkingConfig = new AutoMarkingConfig().initFrom(source, IServerJsonOption.AUTO_MARKING_CONFIG.getFrom(source, jsonObject));
		JsonArray jsonGames = IJsonOption.GAMES.getFrom(source, jsonObject);
		jsonGames.values().stream().map(json -> {
			Game game = new Game(source.forContext(FactoryType.FactoryContext.APPLICATION), source.getFactoryManager());
			game.initFrom(source, json);
			return game;
		}).forEach(games::add);
		return this;
	}
}
