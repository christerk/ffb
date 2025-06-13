package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandReplayControl extends ServerCommand {

	private String coach;

	public ServerCommandReplayControl() {
	}

	public ServerCommandReplayControl(String coach) {
		this.coach = coach;
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_REPLAY_CONTROL;
	}

	public String getCoach() {
		return coach;
	}

	@Override
	public boolean isReplayable() {
		return false;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COACH.addTo(jsonObject, coach);
		return jsonObject;
	}

	public ServerCommandReplayControl initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		coach = IJsonOption.COACH.getFrom(source, jsonObject);
		return this;
	}
}

