package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameCache;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandUploadGame;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerUploadGame extends ServerCommandHandler {

  protected ServerCommandHandlerUploadGame(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_UPLOAD_GAME;
  }

  public void handleNetCommand(NetCommand pNetCommand) {
    InternalServerCommandUploadGame uploadGameCommand = (InternalServerCommandUploadGame) pNetCommand;
    GameCache gameCache = getServer().getGameCache();
    GameState gameState = gameCache.closeGame(uploadGameCommand.getGameId());
    if (gameState == null) {
    	gameState = gameCache.queryFromDb(uploadGameCommand.getGameId());
    }
    if (gameState != null) {
    	gameState.getStepStack().clear();
    	SequenceGenerator.getInstance().pushEndGameSequence(gameState, true);
    	if (StringTool.isProvided(uploadGameCommand.getConcedingTeamId())) {
      	Game game = gameState.getGame();
        game.getGameResult().getTeamResultHome().setConceded(game.getTeamHome().getId().equals(uploadGameCommand.getConcedingTeamId()));
        game.getGameResult().getTeamResultAway().setConceded(game.getTeamAway().getId().equals(uploadGameCommand.getConcedingTeamId()));
    	}
    	gameState.findNextStep(null);
    }
  }
  
}
