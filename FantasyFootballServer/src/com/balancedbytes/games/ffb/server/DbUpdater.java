package com.balancedbytes.games.ffb.server;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.balancedbytes.games.ffb.server.db.DbTransaction;
import com.balancedbytes.games.ffb.server.db.IDbUpdateParameter;
import com.balancedbytes.games.ffb.server.db.IDbUpdateWithGameState;

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
  
  public GameState findGameState(long gameId) {
    synchronized (fUpdateQueue) {
      // find latest update first
      for (int i = fUpdateQueue.size() - 1; i >= 0; i--) {
        DbTransaction transaction = fUpdateQueue.get(i);
        for (IDbUpdateParameter parameter : transaction.getDbUpdateParameters()) {
          if (parameter instanceof IDbUpdateWithGameState) {
            IDbUpdateWithGameState update = (IDbUpdateWithGameState) parameter;
            if (update.getId() == gameId) {
              return update.getGameState();
            }
          }
        }
      }
      return null;
    }
  }

  public FantasyFootballServer getServer() {
    return fServer;
  }

}
