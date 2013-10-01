package com.balancedbytes.games.ffb.server.fumbbl;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.DebugLog;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandFumbblGameCreated;
import com.balancedbytes.games.ffb.util.StringTool;


/**
 * 
 * @author Kalimar
 */
public class FumbblRequestCreateGamestate extends FumbblRequest {
  
  private GameState fGameState;
  
  public FumbblRequestCreateGamestate(GameState pGameState) {
    fGameState = pGameState;
  }

  public GameState getGameState() {
    return fGameState;
  }
  
  @Override
  public void process(FumbblRequestProcessor pRequestProcessor) {
    FantasyFootballServer server = pRequestProcessor.getServer();
    String challengeResponse = pRequestProcessor.getChallengeResponseForFumbblUser();
    Game game = getGameState().getGame();
    if (!game.isTesting()) {
      setRequestUrl(StringTool.bind(server.getProperty(IServerProperty.FUMBBL_GAMESTATE_CREATE), new Object[] { challengeResponse, game.getId(), game.getTeamHome().getId(), game.getTeamAway().getId() }));
    	server.getDebugLog().log(IServerLogLevel.DEBUG, DebugLog.FUMBBL_REQUEST, getRequestUrl());
      FumbblGameState fumbblGameState = pRequestProcessor.processGameStateRequest(getRequestUrl());
      if ((fumbblGameState == null) || !fumbblGameState.isOk()) {
        pRequestProcessor.reportFumbblError(getGameState(), fumbblGameState);
      } else {
        server.getCommunication().handleNetCommand(new InternalServerCommandFumbblGameCreated(game.getId()));
      }
    } else {
      server.getCommunication().handleNetCommand(new InternalServerCommandFumbblGameCreated(game.getId()));
    }
  }

}
