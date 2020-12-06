package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandConfirm extends ClientCommand {

	public ClientCommandConfirm() {
		super();
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_CONFIRM;
	}

	// JSON serialization

	public ClientCommandConfirm initFrom(JsonValue jsonValue) {
		super.initFrom(jsonValue);
		return this;
	}

}
