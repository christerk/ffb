package com.fumbbl.ffb.net;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kalimar
 */
public class NetCommandLog {

	private List<NetCommand> fCommands;

	public NetCommandLog() {
		fCommands = new ArrayList<>();
	}

	public void add(NetCommand pNetCommand) {
		fCommands.add(pNetCommand);
	}

	public NetCommand[] getCommands() {
		return fCommands.toArray(new NetCommand[fCommands.size()]);
	}

}
