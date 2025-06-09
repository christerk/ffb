package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandSetPreventSketching extends ClientCommand {

	private String coach;
	private boolean preventSketching;

	public ClientCommandSetPreventSketching() {
	}

	public ClientCommandSetPreventSketching(String coach, boolean preventSketching) {
		this.coach = coach;
		this.preventSketching = preventSketching;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_SET_PREVENT_SKETCHING;
	}

	public boolean isPreventSketching() {
		return preventSketching;
	}

	public String getCoach() {
		return coach;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PREVENT.addTo(jsonObject, preventSketching);
		IJsonOption.COACH.addTo(jsonObject, coach);
		return jsonObject;
	}

	public ClientCommandSetPreventSketching initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		preventSketching = IJsonOption.PREVENT.getFrom(source, jsonObject);
		coach = IJsonOption.COACH.getFrom(source, jsonObject);
		return this;
	}
}
