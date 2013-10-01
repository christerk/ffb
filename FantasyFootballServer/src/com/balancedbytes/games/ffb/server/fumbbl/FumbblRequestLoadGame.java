package com.balancedbytes.games.ffb.server.fumbbl;

import java.nio.channels.SocketChannel;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameCache;
import com.balancedbytes.games.ffb.server.GameCacheMode;
import com.balancedbytes.games.ffb.server.GameState;
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
  private SocketChannel fSender;
  private int fReplayToCommandNr;

  public FumbblRequestLoadGame(long pGameId) {
    fGameId = pGameId;
  }

  public FumbblRequestLoadGame(long pGameId, int pReplayToCommandNr, SocketChannel pSender) {
    fGameId = pGameId;
    fReplayToCommandNr = pReplayToCommandNr;
    fSender = pSender;
  }

  public FumbblRequestLoadGame(long pGameId, String pCoach, String pTeamId, ClientMode pMode, SocketChannel pSender) {
    fGameId = pGameId;
    fCoach = pCoach;
    fTeamId = pTeamId;
    fMode = pMode;
    fSender = pSender;
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
  
  public SocketChannel getSender() {
    return fSender;
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
          replayCommand.setSender(getSender());
          server.getCommunication().handleNetCommand(replayCommand);
        } else {
          server.getGameCache().add(gameState, GameCacheMode.LOAD_GAME);
        	gameCache.queueDbUpdate(gameState);  // persist status update
          InternalServerCommandJoinApproved joinApprovedCommand = new InternalServerCommandJoinApproved(getGameId(), null, getCoach(), getTeamId(), getMode());
          joinApprovedCommand.setSender(getSender());
          server.getCommunication().handleNetCommand(joinApprovedCommand);
        }
      }
    }
  }
  
}
