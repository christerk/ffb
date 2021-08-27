package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.ServerStatus;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandStatus extends ServerCommand {

	private ServerStatus fServerStatus;
	private String fMessage;

	public ServerCommandStatus() {
		super();
	}

	public ServerCommandStatus(ServerStatus pServerStatus, String pMessage) {
		fServerStatus = pServerStatus;
		fMessage = pMessage;
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_STATUS;
	}

	public ServerStatus getServerStatus() {
		return fServerStatus;
	}

	public String getMessage() {
		return fMessage;
	}

	public boolean isReplayable() {
		return false;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
		IJsonOption.SERVER_STATUS.addTo(jsonObject, fServerStatus);
		IJsonOption.MESSAGE.addTo(jsonObject, fMessage);
		return jsonObject;
	}

	public ServerCommandStatus initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(game, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(game, jsonObject));
		fServerStatus = (ServerStatus) IJsonOption.SERVER_STATUS.getFrom(game, jsonObject);
		fMessage = IJsonOption.MESSAGE.getFrom(game, jsonObject);
		return this;
	}

}
