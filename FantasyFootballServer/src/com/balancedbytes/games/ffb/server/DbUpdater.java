package com.balancedbytes.games.ffb.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.balancedbytes.games.ffb.server.db.DbTransaction;

/**
 * 
 * @author Kalimar
 */
public class DbUpdater implements Runnable {

  private FantasyFootballServer fServer;
  private boolean fStopped;
  private final BlockingQueue<DbTransaction> fUpdateQueue;

  public DbUpdater(FantasyFootballServer pServer) {
    fServer = pServer;
    fUpdateQueue = new LinkedBlockingQueue<DbTransaction>();
  }

  public boolean add(DbTransaction dbTransaction) {
    if (fStopped) {
      return false;
    }
    return fUpdateQueue.offer(dbTransaction);
  }

  @Override
  public void run() {
    try {
      while (!fStopped) {
        DbTransaction update = null;
        try {
          update = fUpdateQueue.take();
        } catch (InterruptedException pInterruptedException) {
          // continue with dbTransaction == null
        }
        handleUpdateInternal(update);
      }
    } catch (Exception pException) {
      getServer().getDebugLog().log(pException);
      System.exit(99);
    }
  }
  
  private void handleUpdateInternal(DbTransaction update) {
    if (update == null) {
      return;
    }
    update.executeUpdate(getServer());
  }

  public void shutdown() {
    fStopped = true;
    List<DbTransaction> updates = new ArrayList<DbTransaction>(); 
    fUpdateQueue.drainTo(updates);
    for (DbTransaction update : updates) {
      handleUpdateInternal(update);
    }
  }

  public FantasyFootballServer getServer() {
    return fServer;
  }

}
