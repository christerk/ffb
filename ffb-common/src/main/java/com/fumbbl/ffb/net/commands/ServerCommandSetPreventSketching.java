package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ServerCommandSetPreventSketching extends ServerCommand {

	private boolean preventSketching;
	private String coach;

	public ServerCommandSetPreventSketching() {
	}

	public ServerCommandSetPreventSketching(String coach, boolean preventSketching) {
		this.preventSketching = preventSketching;
		this.coach = coach;
	}

	public boolean isPreventSketching() {
		return preventSketching;
	}

	public String getCoach() {
		return coach;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.SERVER_SET_PREVENT_SKETCHING;
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.PREVENT.addTo(jsonObject, preventSketching);
		IJsonOption.COACH.addTo(jsonObject, coach);
		return jsonObject;
	}

	public ServerCommandSetPreventSketching initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		coach = IJsonOption.COACH.getFrom(source, jsonObject);
		preventSketching = IJsonOption.PREVENT.getFrom(source, jsonObject);
		return this;
	}
}
