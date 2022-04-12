package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandThrowKeg extends ClientCommand {

	private String playerId;

	public ClientCommandThrowKeg() {
		super();
	}

	public ClientCommandThrowKeg(String pPlayerId) {
		playerId = pPlayerId;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_THROW_KEG;
	}

	public String getPlayerId() {
		return playerId;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		return jsonObject;
	}

	public ClientCommandThrowKeg initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		return this;
	}

}
