package com.fumbbl.ffb.server.request;

import com.fumbbl.ffb.GameStatus;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameCache;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.admin.UtilBackup;

/**
 * 
 * @author Kalimar
 */
public class ServerRequestSaveReplay extends ServerRequest {

	private final long fGameId;

	public ServerRequestSaveReplay(long gameId) {
		fGameId = gameId;
	}

	public long getGameId() {
		return fGameId;
	}

	@Override
	public void process(ServerRequestProcessor pRequestProcessor) {
		FantasyFootballServer server = pRequestProcessor.getServer();
		GameCache gameCache = server.getGameCache();
		GameState gameState = gameCache.getGameStateById(getGameId());
		if (gameState == null) {
			gameState = gameCache.queryFromDb(getGameId());
		}
		if (gameState == null) {
			// game already backed up - nothing to be done
			return;
		}
		boolean backupOk = UtilBackup.save(gameState);
		if (backupOk) {
			gameState.setStatus(GameStatus.BACKUPED);
			server.getGameCache().queueDbUpdate(gameState, false);
			server.getGameCache().queueDbPlayerMarkersUpdate(gameState);
			server.getDebugLog().log(IServerLogLevel.WARN, getGameId(), "Replay stored in file system");
			// request replay to see if backup has been successful, queue delete command
			server.getRequestProcessor()
				.add(new ServerRequestLoadReplay(gameState.getId(), 0, null, ServerRequestLoadReplay.DELETE_GAME, null, null));
		}
	}

}
