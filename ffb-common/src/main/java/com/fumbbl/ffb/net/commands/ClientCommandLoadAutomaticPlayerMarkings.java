package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandLoadAutomaticPlayerMarkings extends ClientCommand {

	private Game game;
	private int index;

	public ClientCommandLoadAutomaticPlayerMarkings() {
		super();
	}

	public ClientCommandLoadAutomaticPlayerMarkings(int index, Game game) {
		this.game = game;
		this.index = index;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_LOAD_AUTOMATIC_PLAYER_MARKINGS;
	}

	public Game getGame() {
		return game;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonValue = super.toJsonValue();
		IJsonOption.SELECTED_INDEX.addTo(jsonValue, index);
		IJsonOption.GAME.addTo(jsonValue, game.toJsonValue());
		return jsonValue;
	}

	@Override
	public ClientCommand initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		JsonObject json = IJsonOption.GAME.getFrom(source, jsonObject);
		game = new Game(source.forContext(FactoryType.FactoryContext.APPLICATION), source.getFactoryManager());
		game.initFrom(source, json);
		index = IJsonOption.SELECTED_INDEX.getFrom(source, jsonObject);
		return this;
	}
}
