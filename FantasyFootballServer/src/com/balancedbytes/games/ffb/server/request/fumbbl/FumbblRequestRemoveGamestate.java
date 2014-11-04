package com.balancedbytes.games.ffb.server.request.fumbbl;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.DebugLog;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.request.ServerRequest;
import com.balancedbytes.games.ffb.server.request.ServerRequestProcessor;
import com.balancedbytes.games.ffb.util.StringTool;


/**
 * 
 * @author Kalimar
 */
public class FumbblRequestRemoveGamestate extends ServerRequest {
  
  private GameState fGameState;
  
  public FumbblRequestRemoveGamestate(GameState pGameState) {
    fGameState = pGameState;
  }

  public GameState getGameState() {
    return fGameState;
  }
  
  @Override
  public void process(ServerRequestProcessor pRequestProcessor) {
    FantasyFootballServer server = pRequestProcessor.getServer();
    Game game = getGameState().getGame();
    String challengeResponse = UtilFumbblRequest.getFumbblAuthChallengeResponseForFumbblUser(server);
    setRequestUrl(StringTool.bind(server.getProperty(IServerProperty.FUMBBL_GAMESTATE_REMOVE), new Object[] { challengeResponse, game.getId() }));
    server.getDebugLog().log(IServerLogLevel.DEBUG, DebugLog.FUMBBL_REQUEST, getRequestUrl());
    FumbblGameState fumbblGameState = UtilFumbblRequest.processFumbblGameStateRequest(server, getRequestUrl());
    if ((fumbblGameState == null) || !fumbblGameState.isOk()) {
      UtilFumbblRequest.reportFumbblError(getGameState(), fumbblGameState);
    }
  }
  
}
