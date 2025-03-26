package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandJoinReplay extends ClientCommand {

	private String replayName;

	public ClientCommandJoinReplay() {
		super();
	}

	public ClientCommandJoinReplay(String replayName) {
		this.replayName = replayName;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_REPLAY_STATUS;
	}

	public String getReplayName() {
		return replayName;
	}

// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.NAME.addTo(jsonObject, replayName);
		return jsonObject;
	}

	public ClientCommandJoinReplay initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		replayName = IJsonOption.NAME.getFrom(source, jsonObject);
		return this;
	}
}

