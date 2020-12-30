package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.ClientStateId;
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

	public ClientCommandDebugClientState initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fClientStateId = (ClientStateId) IJsonOption.CLIENT_STATE_ID.getFrom(game, jsonObject);
		return this;
	}

}
