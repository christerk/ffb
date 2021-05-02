package com.fumbbl.ffb.net.commands;

import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandUseFumblerooskie extends ClientCommand {

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_USE_FUMBLEROOSKIE;
	}
}
