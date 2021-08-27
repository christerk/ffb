package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType.FactoryContext;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandPasswordChallenge extends ServerCommand {

	private String fChallenge;

	public ServerCommandPasswordChallenge() {
		super();
	}

	public ServerCommandPasswordChallenge(String pChallenge) {
		fChallenge = pChallenge;
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_PASSWORD_CHALLENGE;
	}

	public String getChallenge() {
		return fChallenge;
	}

	public boolean isReplayable() {
		return false;
	}

	@Override
	public FactoryContext getContext() {
		return FactoryContext.APPLICATION;
	}
	
	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
		IJsonOption.CHALLENGE.addTo(jsonObject, fChallenge);
		return jsonObject;
	}

	public ServerCommandPasswordChallenge initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(game, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(game, jsonObject));
		fChallenge = IJsonOption.CHALLENGE.getFrom(game, jsonObject);
		return this;
	}

}
