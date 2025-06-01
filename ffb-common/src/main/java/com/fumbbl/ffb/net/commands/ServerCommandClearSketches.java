package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.net.NetCommandId;

public class ServerCommandClearSketches extends ServerCommand {

	public ServerCommandClearSketches() {
		super();
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_CLEAR_SKETCHES;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		return new JsonObject();
	}

	public ServerCommandClearSketches initFrom(IFactorySource source, JsonValue jsonValue) {
		return this;
	}

}
