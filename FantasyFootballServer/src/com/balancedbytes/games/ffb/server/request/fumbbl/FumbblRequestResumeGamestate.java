package com.balancedbytes.games.ffb.server.request.fumbbl;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
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
public class FumbblRequestResumeGamestate extends ServerRequest {
  
  private GameState fGameState;
  
  public FumbblRequestResumeGamestate(GameState pGameState) {
    fGameState = pGameState;
  }

  public GameState getGameState() {
    return fGameState;
  }
  
  @Override
  public void process(ServerRequestProcessor pRequestProcessor) {
    FantasyFootballServer server = pRequestProcessor.getServer();
    String challengeResponse = UtilFumbblRequest.getFumbblAuthChallengeResponseForFumbblUser(server);
    Game game = getGameState().getGame();
    if (!game.isTesting()) {
      GameResult gameResult = game.getGameResult();
      int spectators = getGameState().getServer().getSessionManager().getSessionsOfSpectators(game.getId()).length;
      setRequestUrl(StringTool.bind(server.getProperty(IServerProperty.FUMBBL_GAMESTATE_RESUME),
        new Object[] {
          challengeResponse,
          game.getId(),
          game.getTeamHome().getId(),
          game.getTeamAway().getId(),
          game.getHalf(),
          game.getTurnData().getTurnNr(),
          gameResult.getTeamResultHome().getScore(),
          gameResult.getTeamResultAway().getScore(),
          spectators
        }
      ));
      server.getDebugLog().log(IServerLogLevel.WARN, DebugLog.FUMBBL_REQUEST, getRequestUrl());
      FumbblGameState fumbblGameState = UtilFumbblRequest.processFumbblGameStateRequest(server, getRequestUrl());
      if ((fumbblGameState == null) || !fumbblGameState.isOk()) {
        UtilFumbblRequest.reportFumbblError(getGameState(), fumbblGameState);
      }
    }
  }

}
