package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandIllegalProcedure extends ClientCommand {

	public ClientCommandIllegalProcedure() {
		super();
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_ILLEGAL_PROCEDURE;
	}

	// JSON serialization

	public ClientCommandIllegalProcedure initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		return this;
	}

}
