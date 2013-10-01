package com.balancedbytes.games.ffb.server;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.balancedbytes.games.ffb.server.db.DbTransaction;

/**
 * 
 * @author Kalimar
 */
public class DbUpdater implements Runnable {

  private FantasyFootballServer fServer;
  private boolean fStopped;
  private List<DbTransaction> fUpdateQueue;

  public DbUpdater(FantasyFootballServer pServer) {
    fServer = pServer;
    fUpdateQueue = Collections.synchronizedList(new LinkedList<DbTransaction>());
  }

  public void add(DbTransaction pDbTransaction) {
    synchronized (fUpdateQueue) {
      fUpdateQueue.add(pDbTransaction);
      fUpdateQueue.notify();
    }
  }

  public void run() {
    try {
      while (true) {
        DbTransaction dbTransaction = null;
        synchronized (fUpdateQueue) {
          try {
            while (fUpdateQueue.isEmpty() && !fStopped) {
              fUpdateQueue.wait();
            }
          } catch (InterruptedException e) {
            break;
          }
          if (fStopped) {
            break;
          }
          dbTransaction = fUpdateQueue.remove(0);
        }
        dbTransaction.executeUpdate(getServer());
      }
    } catch (Exception pException) {
      getServer().getDebugLog().log(pException);
      System.exit(99);
    }
  }

  public void stop() {
    fStopped = true;
    synchronized (fUpdateQueue) {
      fUpdateQueue.notifyAll();
    }
  }

  public FantasyFootballServer getServer() {
    return fServer;
  }

}
