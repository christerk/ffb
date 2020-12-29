package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandStartGame extends ClientCommand {

	public ClientCommandStartGame() {
		super();
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_START_GAME;
	}

	// JSON serialization

	public ClientCommandStartGame initFrom(Game game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		return this;
	}

}
