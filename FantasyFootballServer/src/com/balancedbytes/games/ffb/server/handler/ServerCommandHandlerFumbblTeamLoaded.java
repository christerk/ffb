package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.fumbbl.FumbblRequestCheckGamestate;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandFumbblTeamLoaded;
import com.balancedbytes.games.ffb.server.util.UtilStartGame;
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

  public void handleNetCommand(NetCommand pNetCommand) {
    InternalServerCommandFumbblTeamLoaded teamLoadedCommand = (InternalServerCommandFumbblTeamLoaded) pNetCommand;
    GameState gameState = getServer().getGameCache().getGameStateById(teamLoadedCommand.getGameId());
    if (gameState == null) {
    	return;
    }
  	Game game = gameState.getGame();
    if (GameStatus.SCHEDULED == gameState.getStatus()) {
    	if (StringTool.isProvided(game.getTeamHome().getId()) && StringTool.isProvided(game.getTeamAway().getId()) && (teamLoadedCommand.getAdminGameIdListener() != null)) {
    		teamLoadedCommand.getAdminGameIdListener().setGameId(gameState.getId());
    	}
    } else {
      if (UtilStartGame.joinGameAsPlayerAndCheckIfReadyToStart(gameState, teamLoadedCommand.getSender(), teamLoadedCommand.getCoach(), teamLoadedCommand.isHomeTeam())) {
        getServer().getFumbblRequestProcessor().add(new FumbblRequestCheckGamestate(gameState));
      }
    }
  }

}
