package com.fumbbl.ffb.server.net;

import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.db.DbConnectionManager;

import java.util.TimerTask;

public class ServerDbKeepAliveTask extends TimerTask {

	private final FantasyFootballServer fServer;
	private final DbConnectionManager fDbConnectionManager;

	public ServerDbKeepAliveTask(FantasyFootballServer server, DbConnectionManager dbConnectionManager) {
		fServer = server;
		fDbConnectionManager = dbConnectionManager;
	}

	public void run() {
		try {
			getDbConnectionManager().doKeepAlivePing();
		} catch (Exception anyException) {
			getServer().getDebugLog().logWithOutGameId(anyException);
			System.exit(99);
		}
	}

	public FantasyFootballServer getServer() {
		return fServer;
	}

	public DbConnectionManager getDbConnectionManager() {
		return fDbConnectionManager;
	}

}
