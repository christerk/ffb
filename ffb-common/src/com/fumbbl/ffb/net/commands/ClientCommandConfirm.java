package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.net.NetCommandId;

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

	public ClientCommandConfirm initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		return this;
	}

}
