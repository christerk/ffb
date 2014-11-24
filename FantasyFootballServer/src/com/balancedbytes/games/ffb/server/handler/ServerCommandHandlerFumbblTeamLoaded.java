package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandFumbblTeamLoaded;
import com.balancedbytes.games.ffb.server.request.fumbbl.FumbblRequestCheckGamestate;
import com.balancedbytes.games.ffb.server.util.UtilServerStartGame;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerFumbblTeamLoaded extends ServerCommandHandler {

  protected ServerCommandHandlerFumbblTeamLoaded(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_FUMBBL_TEAM_LOADED;
  }

  public void handleCommand(ReceivedCommand pReceivedCommand) {
    InternalServerCommandFumbblTeamLoaded teamLoadedCommand = (InternalServerCommandFumbblTeamLoaded) pReceivedCommand.getCommand();
    GameState gameState = getServer().getGameCache().getGameStateById(teamLoadedCommand.getGameId());
    if (gameState == null) {
    	return;
    }
  	Game game = gameState.getGame();
    if (GameStatus.SCHEDULED == gameState.getStatus()) {
    	if (StringTool.isProvided(game.getTeamHome().getId()) && StringTool.isProvided(game.getTeamAway().getId())) {
    	  getServer().getGameCache().queueDbUpdate(gameState, true);
    	}
    } else {
      if (UtilServerStartGame.joinGameAsPlayerAndCheckIfReadyToStart(gameState, pReceivedCommand.getSession(), teamLoadedCommand.getCoach(), teamLoadedCommand.isHomeTeam())) {
        getServer().getRequestProcessor().add(new FumbblRequestCheckGamestate(gameState));
      }
    }
  }

}
