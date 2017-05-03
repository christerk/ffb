package com.balancedbytes.games.ffb.server.net;

import java.util.TimerTask;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.DbConnectionManager;

public class ServerDbKeepAliveTask extends TimerTask {

  private FantasyFootballServer fServer;
  private DbConnectionManager fDbConnectionManager;

  public ServerDbKeepAliveTask(FantasyFootballServer server, DbConnectionManager dbConnectionManager) {
    fServer = server;
    fDbConnectionManager = dbConnectionManager;
  }

  public void run() {
    try {
      getDbConnectionManager().doKeepAlivePing();
    } catch (Exception anyException) {
      getServer().getDebugLog().log(anyException);
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
