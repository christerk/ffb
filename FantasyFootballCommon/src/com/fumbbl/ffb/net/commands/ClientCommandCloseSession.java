package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandCloseSession extends ClientCommand {

	public ClientCommandCloseSession() {
		super();
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_CLOSE_SESSION;
	}

	// JSON serialization

	public ClientCommandCloseSession initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		return this;
	}

}
