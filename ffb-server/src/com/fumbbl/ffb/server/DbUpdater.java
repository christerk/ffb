package com.fumbbl.ffb.server;

import com.fumbbl.ffb.server.db.DbTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * @author Kalimar
 */
public class DbUpdater implements Runnable {

	private final FantasyFootballServer fServer;
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
			getServer().getDebugLog().logWithOutGameId(pException);
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
		List<DbTransaction> updates = new ArrayList<>();
		fUpdateQueue.drainTo(updates);
		for (DbTransaction update : updates) {
			handleUpdateInternal(update);
		}
	}

	public FantasyFootballServer getServer() {
		return fServer;
	}

}
