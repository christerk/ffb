package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandTeamSetupDelete extends ClientCommand {

	private String fSetupName;

	public ClientCommandTeamSetupDelete() {
		super();
	}

	public ClientCommandTeamSetupDelete(String pSetupName) {
		fSetupName = pSetupName;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_TEAM_SETUP_DELETE;
	}

	public String getSetupName() {
		return fSetupName;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.SETUP_NAME.addTo(jsonObject, fSetupName);
		return jsonObject;
	}

	public ClientCommandTeamSetupDelete initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fSetupName = IJsonOption.SETUP_NAME.getFrom(game, jsonObject);
		return this;
	}

}
