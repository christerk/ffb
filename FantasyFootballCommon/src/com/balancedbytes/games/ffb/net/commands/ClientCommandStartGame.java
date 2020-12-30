package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.FactoryType.FactoryContext;
import com.balancedbytes.games.ffb.factory.IFactorySource;
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


	@Override
	public FactoryContext getContext() {
		return FactoryContext.APPLICATION;
	}
	
	// JSON serialization

	public ClientCommandStartGame initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		return this;
	}

}
