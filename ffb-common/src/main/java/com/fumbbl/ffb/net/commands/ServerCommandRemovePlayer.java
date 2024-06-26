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
public class ServerCommandRemovePlayer extends ServerCommand {

	private String fPlayerId;

	public ServerCommandRemovePlayer() {
		super();
	}

	public ServerCommandRemovePlayer(String pPlayerId) {
		fPlayerId = pPlayerId;
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_REMOVE_PLAYER;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		return jsonObject;
	}

	public ServerCommandRemovePlayer initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(source, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		return this;
	}

}
