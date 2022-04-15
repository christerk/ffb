package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandDebugClientState extends ClientCommand {

	private ClientStateId fClientStateId;

	public ClientCommandDebugClientState() {
		super();
	}

	public ClientCommandDebugClientState(ClientStateId pClientStateId) {
		this();
		fClientStateId = pClientStateId;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_DEBUG_CLIENT_STATE;
	}

	public ClientStateId getClientStateId() {
		return fClientStateId;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.CLIENT_STATE_ID.addTo(jsonObject, fClientStateId);
		return jsonObject;
	}

	public ClientCommandDebugClientState initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fClientStateId = (ClientStateId) IJsonOption.CLIENT_STATE_ID.getFrom(source, jsonObject);
		return this;
	}

}
