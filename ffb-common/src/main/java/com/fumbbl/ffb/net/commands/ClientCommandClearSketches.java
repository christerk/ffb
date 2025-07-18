package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandClearSketches extends ClientSketchCommand {

	public ClientCommandClearSketches() {
		super();
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_CLEAR_SKETCHES;
	}

	@Override
	public boolean requiresControl() {
		return true;
	}

// JSON serialization

	public JsonObject toJsonValue() {
		return super.toJsonValue();
	}

	public ClientCommandClearSketches initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		return this;
	}

}
