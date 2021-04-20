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
public class ServerCommandPong extends ServerCommand {

	private long fTimestamp;

	public ServerCommandPong() {
		super();
	}

	public ServerCommandPong(long pTimestamp) {
		fTimestamp = pTimestamp;
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_PONG;
	}

	public long getTimestamp() {
		return fTimestamp;
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
		IJsonOption.TIMESTAMP.addTo(jsonObject, fTimestamp);
		return jsonObject;
	}

	public ServerCommandPong initFrom(IFactorySource source, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		fTimestamp = IJsonOption.TIMESTAMP.getFrom(source, jsonObject);
		return this;
	}

}
