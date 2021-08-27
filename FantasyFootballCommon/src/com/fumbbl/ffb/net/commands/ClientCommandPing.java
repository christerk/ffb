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
