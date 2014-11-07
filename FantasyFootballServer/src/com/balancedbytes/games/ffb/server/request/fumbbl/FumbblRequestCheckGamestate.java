package com.balancedbytes.games.ffb.server.request.fumbbl;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.server.DebugLog;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandFumbblGameChecked;
import com.balancedbytes.games.ffb.server.request.ServerRequest;
import com.balancedbytes.games.ffb.server.request.ServerRequestProcessor;
import com.balancedbytes.games.ffb.util.StringTool;


/**
 * 
 * @author Kalimar
 */
public class FumbblRequestCheckGamestate extends ServerRequest {
  
  private GameState fGameState;
  
  public FumbblRequestCheckGamestate(GameState pGameState) {
    fGameState = pGameState;
  }
  
  public GameState getGameState() {
    return fGameState;
  }
  
  @Override
  public void process(ServerRequestProcessor pRequestProcessor) {
    Game game = getGameState().getGame();
    FantasyFootballServer server = pRequestProcessor.getServer();
    if (!game.isTesting()) {
      setRequestUrl(StringTool.bind(server.getProperty(IServerProperty.FUMBBL_GAMESTATE_CHECK), new Object[] { game.getTeamHome().getId(), game.getTeamAway().getId() }));
    	server.getDebugLog().log(IServerLogLevel.DEBUG, DebugLog.FUMBBL_REQUEST, getRequestUrl());
      FumbblGameState fumbblGameState = UtilFumbblRequest.processFumbblGameStateRequest(server, getRequestUrl());
      if ((fumbblGameState == null) || !fumbblGameState.isOk()) {
        UtilFumbblRequest.reportFumbblError(getGameState(), fumbblGameState);
      } else {
        game.getOptions().init(fumbblGameState.getOptions());
        server.getDebugLog().log(IServerLogLevel.TRACE, getGameState().getId(), game.getOptions().toJsonValue().toString());
      	game.setTesting(game.isTesting() || UtilGameOption.isOptionEnabled(game, GameOptionId.TEST_MODE));
      	InternalServerCommandFumbblGameChecked gameCheckedCommand = new InternalServerCommandFumbblGameChecked(getGameState().getId());
        server.getCommunication().handleCommand(gameCheckedCommand);
      }
    } else {
      InternalServerCommandFumbblGameChecked gameCheckedCommand = new InternalServerCommandFumbblGameChecked(getGameState().getId());
      server.getCommunication().handleCommand(gameCheckedCommand);
    }
  }
  
  

}
