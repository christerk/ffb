package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandUseTeamMatesWisdom extends ClientCommand {

	private String playerId;

	public ClientCommandUseTeamMatesWisdom() {
		super();
	}

	public ClientCommandUseTeamMatesWisdom(String playerId) {
		this.playerId = playerId;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_USE_TEAM_MATES_WISDOM;
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

	public ClientCommandUseTeamMatesWisdom initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		return this;
	}
}
