package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ServerCommandUnzapPlayer extends ServerCommand {
	private String teamId;
	private String playerId;

	public ServerCommandUnzapPlayer(String playerId, String teamId) {
		this.teamId = teamId;
		this.playerId = playerId;
	}

	public ServerCommandUnzapPlayer() {
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.SERVER_UNZAP_PLAYER;
	}

	public String getTeamId() {
		return teamId;
	}

	public String getPlayerId() {
		return playerId;
	}

	@Override
	public Object initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(source, jsonObject));
		teamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		return jsonObject;
	}
}
