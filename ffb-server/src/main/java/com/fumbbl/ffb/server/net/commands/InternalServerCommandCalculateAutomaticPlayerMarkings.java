package com.fumbbl.ffb.server.net.commands;

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

public class InternalServerCommandCalculateAutomaticPlayerMarkings extends InternalServerCommand {

	private AutoMarkingConfig autoMarkingConfig;
	private Game game;
	private int index;

	public InternalServerCommandCalculateAutomaticPlayerMarkings(AutoMarkingConfig autoMarkingConfig, int index, Game game) {
		super();
		this.autoMarkingConfig = autoMarkingConfig;
		this.game = game;
		this.index = index;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.INTERNAL_CALCULATE_AUTOMATIC_PLAYER_MARKINGS;
	}

	public AutoMarkingConfig getAutoMarkingConfig() {
		return autoMarkingConfig;
	}

	public Game getGame() {
		return game;
	}

	public int getIndex() {
		return index;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.AUTO_MARKING_CONFIG.addTo(jsonObject, UtilJson.toJsonObject(autoMarkingConfig.toJsonValue()));
		IJsonOption.GAME.addTo(jsonObject, game.toJsonValue());
		IJsonOption.SELECTED_INDEX.addTo(jsonObject, index);
		return jsonObject;
	}

	public InternalServerCommandCalculateAutomaticPlayerMarkings initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		autoMarkingConfig = new AutoMarkingConfig().initFrom(source, IServerJsonOption.AUTO_MARKING_CONFIG.getFrom(source, jsonObject));
		JsonObject json = IJsonOption.GAME.getFrom(source, jsonObject);
		game = new Game(source.forContext(FactoryType.FactoryContext.APPLICATION), source.getFactoryManager());
		game.initFrom(source, json);
		index = IJsonOption.SELECTED_INDEX.getFrom(source, jsonObject);
		return this;
	}
}
