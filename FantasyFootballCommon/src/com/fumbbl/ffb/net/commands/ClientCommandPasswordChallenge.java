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
public class ClientCommandPasswordChallenge extends ClientCommand {

	private String fCoach;

	public ClientCommandPasswordChallenge() {
		super();
	}

	public ClientCommandPasswordChallenge(String pChallenge) {
		fCoach = pChallenge;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_PASSWORD_CHALLENGE;
	}

	public String getCoach() {
		return fCoach;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.COACH.addTo(jsonObject, fCoach);
		return jsonObject;
	}

	public ClientCommandPasswordChallenge initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fCoach = IJsonOption.COACH.getFrom(game, jsonObject);
		return this;
	}

}
