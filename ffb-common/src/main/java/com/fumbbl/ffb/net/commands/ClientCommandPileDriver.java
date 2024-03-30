package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandPileDriver extends ClientCommand {
	private String playerId;

	public ClientCommandPileDriver() {
	}

	public ClientCommandPileDriver(String playerId) {
		this.playerId = playerId;
	}

	public String getPlayerId() {
		return playerId;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_PILE_DRIVER;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		return jsonObject;
	}

	@Override
	public ClientCommand initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		playerId = IJsonOption.PLAYER_ID.getFrom(source, UtilJson.toJsonObject(jsonValue));
		return this;
	}
}
