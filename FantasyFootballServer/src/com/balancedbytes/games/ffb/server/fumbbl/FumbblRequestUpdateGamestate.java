package com.balancedbytes.games.ffb.server.fumbbl;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.server.DebugLog;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.util.StringTool;


/**
 * 
 * @author Kalimar
 */
public class FumbblRequestUpdateGamestate extends FumbblRequest {

  private GameState fGameState;
  
  public FumbblRequestUpdateGamestate(GameState pGameState) {
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
      GameResult gameResult = game.getGameResult();
      int spectators = getGameState().getServer().getChannelManager().getChannelsOfSpectators(getGameState()).length;
      setRequestUrl(StringTool.bind(server.getProperty(IServerProperty.FUMBBL_GAMESTATE_UPDATE), new Object[] { challengeResponse, game.getId(), game.getHalf(), game.getTurnData().getTurnNr(), gameResult.getTeamResultHome().getScore(), gameResult.getTeamResultAway().getScore(), spectators }));
      server.getDebugLog().log(IServerLogLevel.DEBUG, DebugLog.FUMBBL_REQUEST, getRequestUrl());
      FumbblGameState fumbblGameState = pRequestProcessor.processGameStateRequest(getRequestUrl());
      if ((fumbblGameState == null) || !fumbblGameState.isOk()) {
        pRequestProcessor.reportFumbblError(getGameState(), fumbblGameState);
      }
    }
  }
  
}
