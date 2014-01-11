package com.balancedbytes.games.ffb.server.fumbbl;

import org.eclipse.jetty.websocket.api.Session;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameCache;
import com.balancedbytes.games.ffb.server.GameCacheMode;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandJoinApproved;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandReplayLoaded;


/**
 * 
 * @author Kalimar
 */
public class FumbblRequestLoadGame extends FumbblRequest {

  private long fGameId;
  private String fCoach;
  private String fTeamId;
  private ClientMode fMode;
  private Session fSession;
  private int fReplayToCommandNr;

  public FumbblRequestLoadGame(long pGameId) {
    fGameId = pGameId;
  }

  public FumbblRequestLoadGame(long pGameId, int pReplayToCommandNr, Session pSession) {
    fGameId = pGameId;
    fReplayToCommandNr = pReplayToCommandNr;
    fSession = pSession;
  }

  public FumbblRequestLoadGame(long pGameId, String pCoach, String pTeamId, ClientMode pMode, Session pSession) {
    fGameId = pGameId;
    fCoach = pCoach;
    fTeamId = pTeamId;
    fMode = pMode;
    fSession = pSession;
  }

  public long getGameId() {
    return fGameId;
  }
  
  public String getCoach() {
    return fCoach;
  }
  
  public String getTeamId() {
    return fTeamId;
  }
  
  public ClientMode getMode() {
    return fMode;
  }
  
  public Session getSession() {
    return fSession;
  }
  
  public int getReplayToCommandNr() {
    return fReplayToCommandNr;
  }
  
  @Override
  public void process(FumbblRequestProcessor pRequestProcessor) {
    FantasyFootballServer server = pRequestProcessor.getServer();
    GameCache gameCache = server.getGameCache();
    if (!server.isBlockingNewGames()) {
    	GameState gameState = gameCache.queryFromDb(getGameId());
      if (gameState != null) {
        if (ClientMode.REPLAY == getMode()) {
          server.getGameCache().add(gameState, GameCacheMode.REPLAY_GAME);
          InternalServerCommandReplayLoaded replayCommand = new InternalServerCommandReplayLoaded(getGameId(), getReplayToCommandNr());
          server.getCommunication().handleCommand(new ReceivedCommand(replayCommand, getSession()));
        } else {
          server.getGameCache().add(gameState, GameCacheMode.LOAD_GAME);
        	gameCache.queueDbUpdate(gameState);  // persist status update
          InternalServerCommandJoinApproved joinApprovedCommand = new InternalServerCommandJoinApproved(getGameId(), null, getCoach(), getTeamId(), getMode());
          server.getCommunication().handleCommand(new ReceivedCommand(joinApprovedCommand, getSession()));
        }
      }
    }
  }
  
}
