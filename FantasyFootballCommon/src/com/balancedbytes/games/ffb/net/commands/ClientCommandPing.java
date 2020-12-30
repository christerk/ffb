package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.FactoryType.FactoryContext;
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
public class ClientCommandPing extends ClientCommand {

	private long fTimestamp;

	public ClientCommandPing() {
		super();
	}

	public ClientCommandPing(long pTimestamp) {
		fTimestamp = pTimestamp;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_PING;
	}

	public long getTimestamp() {
		return fTimestamp;
	}

	@Override
	public FactoryContext getContext() {
		return FactoryContext.APPLICATION;
	}
	
	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.TIMESTAMP.addTo(jsonObject, fTimestamp);
		return jsonObject;
	}

	public ClientCommandPing initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(game, jsonObject));
		fTimestamp = IJsonOption.TIMESTAMP.getFrom(game, jsonObject);
		return this;
	}

}
