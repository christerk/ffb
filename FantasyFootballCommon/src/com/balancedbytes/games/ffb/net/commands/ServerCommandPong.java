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

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.TIMESTAMP.addTo(jsonObject, fTimestamp);
		return jsonObject;
	}

	public ServerCommandPong initFrom(JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
		fTimestamp = IJsonOption.TIMESTAMP.getFrom(jsonObject);
		return this;
	}

}
