package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
		IJsonOption.CHALLENGE.addTo(jsonObject, fChallenge);
		return jsonObject;
	}

	public ServerCommandPasswordChallenge initFrom(JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(jsonObject));
		fChallenge = IJsonOption.CHALLENGE.getFrom(jsonObject);
		return this;
	}

}
