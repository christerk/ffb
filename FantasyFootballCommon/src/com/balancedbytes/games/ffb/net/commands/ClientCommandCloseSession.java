package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonValue;

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

	public ClientCommandCloseSession initFrom(Game game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		return this;
	}

}
