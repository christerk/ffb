package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandJoinReplay extends ClientCommand {

	private String replayName, coach;

	public ClientCommandJoinReplay() {
		super();
	}

	public ClientCommandJoinReplay(String replayName, String coach) {
		this.replayName = replayName;
		this.coach = coach;
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

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.NAME.addTo(jsonObject, replayName);
		IJsonOption.COACH.addTo(jsonObject, coach);
		return jsonObject;
	}

	public ClientCommandJoinReplay initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		replayName = IJsonOption.NAME.getFrom(source, jsonObject);
		coach = IJsonOption.COACH.getFrom(source, jsonObject);
		return this;
	}
}

