package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandRequestVersion extends ClientCommand {

	public ClientCommandRequestVersion() {
		super();
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_REQUEST_VERSION;
	}

	// JSON serialization

	public ClientCommandRequestVersion initFrom(Game game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		return this;
	}

}
