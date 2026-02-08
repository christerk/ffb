package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandPuntToCrowd extends ClientCommand {

	private boolean puntToCrowd;

	public ClientCommandPuntToCrowd() {

	}

	public ClientCommandPuntToCrowd(boolean puntToCrowd) {
		this.puntToCrowd = puntToCrowd;
	}

	public boolean isPuntToCrowd() {
		return puntToCrowd;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_PUNT_TO_CROWD;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PUNT_TO_CROWD.addTo(jsonObject, puntToCrowd);
		return jsonObject;
	}

	public ClientCommandPuntToCrowd initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		puntToCrowd = IJsonOption.PUNT_TO_CROWD.getFrom(source, jsonObject);
		return this;
	}

}
