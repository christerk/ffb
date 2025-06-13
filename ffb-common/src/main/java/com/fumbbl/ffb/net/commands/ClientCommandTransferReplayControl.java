package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;


public class ClientCommandTransferReplayControl extends ClientCommand {

	private String coach;

	public ClientCommandTransferReplayControl() {
		super();
	}

	public ClientCommandTransferReplayControl(String coach) {
		this.coach = coach;
	}


	public NetCommandId getId() {
		return NetCommandId.CLIENT_TRANSFER_REPLAY_CONTROL;
	}

	public String getCoach() {
		return coach;
	}

// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.COACH.addTo(jsonObject, coach);
		return jsonObject;
	}

	public ClientCommandTransferReplayControl initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		coach = IJsonOption.COACH.getFrom(source, jsonObject);
		return this;
	}

}
