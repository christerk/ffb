package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class ServerCommandZapPlayer extends ServerCommand {
	private String teamId;
	private String playerId;

	public ServerCommandZapPlayer(String playerId, String teamId) {
		this.teamId = teamId;
		this.playerId = playerId;
	}

	public ServerCommandZapPlayer() {
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.SERVER_ZAP_PLAYER;
	}

	public String getTeamId() {
		return teamId;
	}

	public String getPlayerId() {
		return playerId;
	}

	@Override
	public Object initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(game, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(game, jsonObject));
		teamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		playerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
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
