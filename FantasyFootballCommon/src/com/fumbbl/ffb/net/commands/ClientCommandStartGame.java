package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType.FactoryContext;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.net.NetCommandId;

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

	public ClientCommandStartGame initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		return this;
	}

}
