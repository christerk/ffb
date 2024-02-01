package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public abstract class ServerCommandHandler implements IReceivedCommandHandler {

	private final FantasyFootballServer fServer;

	protected ServerCommandHandler(FantasyFootballServer pServer) {
		fServer = pServer;
	}

	public abstract NetCommandId getId();

	protected FantasyFootballServer getServer() {
		return fServer;
	}

	protected boolean isServerInTestMode() {
		String testSetting = getServer().getProperty(IServerProperty.SERVER_TEST);
		return StringTool.isProvided(testSetting) && Boolean.parseBoolean(testSetting);
	}
}
