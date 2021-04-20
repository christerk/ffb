package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandEndTurn extends ClientCommand {

	public ClientCommandEndTurn() {
		super();
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_END_TURN;
	}

	// JSON serialization

	public ClientCommandEndTurn initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		return this;
	}

}
