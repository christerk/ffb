package com.balancedbytes.games.ffb.server.fumbbl;

import com.balancedbytes.games.ffb.GameOption;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.DebugLog;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandFumbblGameChecked;
import com.balancedbytes.games.ffb.util.StringTool;


/**
 * 
 * @author Kalimar
 */
public class FumbblRequestCheckGamestate extends FumbblRequest {
  
  private GameState fGameState;
  
  public FumbblRequestCheckGamestate(GameState pGameState) {
    fGameState = pGameState;
  }
  
  public GameState getGameState() {
    return fGameState;
  }
  
  @Override
  public void process(FumbblRequestProcessor pRequestProcessor) {
    Game game = getGameState().getGame();
    FantasyFootballServer server = pRequestProcessor.getServer();
    if (!game.isTesting()) {
      setRequestUrl(StringTool.bind(server.getProperty(IServerProperty.FUMBBL_GAMESTATE_CHECK), new Object[] { game.getTeamHome().getId(), game.getTeamAway().getId() }));
    	server.getDebugLog().log(IServerLogLevel.DEBUG, DebugLog.FUMBBL_REQUEST, getRequestUrl());
      FumbblGameState fumbblGameState = pRequestProcessor.processGameStateRequest(getRequestUrl());
      if ((fumbblGameState == null) || !fumbblGameState.isOk()) {
        pRequestProcessor.reportFumbblError(getGameState(), fumbblGameState);
      } else {
        game.getOptions().init(fumbblGameState.getOptions());
      	game.setTesting(game.isTesting() || game.getOptions().getOptionValue(GameOption.TEST_MODE).isEnabled());
        server.getCommunication().handleNetCommand(new InternalServerCommandFumbblGameChecked(getGameState().getId()));
      }
    } else {
      server.getCommunication().handleNetCommand(new InternalServerCommandFumbblGameChecked(getGameState().getId()));
    }
  }

}
