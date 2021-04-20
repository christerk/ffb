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
public class ClientCommandTeamSetupLoad extends ClientCommand {

	private String fSetupName;

	public ClientCommandTeamSetupLoad() {
		super();
	}

	public ClientCommandTeamSetupLoad(String pSetupName) {
		fSetupName = pSetupName;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_TEAM_SETUP_LOAD;
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

	public ClientCommandTeamSetupLoad initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fSetupName = IJsonOption.SETUP_NAME.getFrom(game, jsonObject);
		return this;
	}

}
