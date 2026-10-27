package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandJoinReplay extends ClientCommand {

	private String replayName, coach, protocolVersion, clientName, clientVersion;
	private long gameId;

	public ClientCommandJoinReplay() {
		super();
	}

	public ClientCommandJoinReplay(String replayName, String coach, long gameId, String protocolVersion,
																 String clientName, String clientVersion) {
		this.replayName = replayName;
		this.coach = coach;
		this.gameId = gameId;
		this.protocolVersion = protocolVersion;
		this.clientName = clientName;
		this.clientVersion = clientVersion;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_JOIN_REPLAY;
	}

	public String getReplayName() {
		return replayName;
	}

	public String getCoach() {
		return coach;
	}

	public long getGameId() {
		return gameId;
	}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public String getClientName() {
		return clientName;
	}

	public String getClientVersion() {
		return clientVersion;
	}

// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.NAME.addTo(jsonObject, replayName);
		IJsonOption.COACH.addTo(jsonObject, coach);
		IJsonOption.GAME_ID.addTo(jsonObject, gameId);
		IJsonOption.PROTOCOL_VERSION.addTo(jsonObject, protocolVersion);
		IJsonOption.CLIENT_NAME.addTo(jsonObject, clientName);
		IJsonOption.CLIENT_VERSION.addTo(jsonObject, clientVersion);
		return jsonObject;
	}

	public ClientCommandJoinReplay initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		replayName = IJsonOption.NAME.getFrom(source, jsonObject);
		coach = IJsonOption.COACH.getFrom(source, jsonObject);
		gameId = IJsonOption.GAME_ID.getFrom(source, jsonObject);
		protocolVersion = IJsonOption.PROTOCOL_VERSION.getFrom(source, jsonObject);
		clientName = IJsonOption.CLIENT_NAME.getFrom(source, jsonObject);
		clientVersion = IJsonOption.CLIENT_VERSION.getFrom(source, jsonObject);
		return this;
	}
}

