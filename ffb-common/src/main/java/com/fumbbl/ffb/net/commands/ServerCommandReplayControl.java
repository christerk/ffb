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
public class ServerCommandReplayControl extends ServerCommand {

	private boolean control;

	public ServerCommandReplayControl() {
	}

	public ServerCommandReplayControl(boolean control) {
		this.control = control;
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_REPLAY_CONTROL;
	}

	public boolean isControl() {
		return control;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.CONTROL.addTo(jsonObject, control);
		return jsonObject;
	}

	public ServerCommandReplayControl initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		control = IJsonOption.CONTROL.getFrom(source, jsonObject);
		return this;
	}
}

