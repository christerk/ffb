package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameCache;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandUploadGame;
import com.balancedbytes.games.ffb.server.request.ServerRequestLoadReplay;
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

  public void handleCommand(ReceivedCommand receivedCommand) {
    InternalServerCommandUploadGame uploadGameCommand = (InternalServerCommandUploadGame) receivedCommand.getCommand();
    GameCache gameCache = getServer().getGameCache();
    GameState gameState = gameCache.closeGame(uploadGameCommand.getGameId());
    if (gameState == null) {
      gameState = gameCache.queryFromDb(uploadGameCommand.getGameId());
//      if (gameState != null) {
//        getServer().getDebugLog().log(IServerLogLevel.WARN, uploadGameCommand.getGameId(), "ServerCommandUploadGame loaded from db");
//      }
    }
    if (gameState == null) {
      // game has been moved out of the db - request it from the backup service
      getServer().getRequestProcessor().add(
        new ServerRequestLoadReplay(uploadGameCommand.getGameId(), 0, receivedCommand.getSession(), ServerRequestLoadReplay.UPLOAD_GAME)
      );
    } else {
      gameState.getStepStack().clear();
      if (StringTool.isProvided(uploadGameCommand.getConcedingTeamId())) {
        Game game = gameState.getGame();
        game.getGameResult().getTeamResultHome().setConceded(game.getTeamHome().getId().equals(uploadGameCommand.getConcedingTeamId()));
        game.getGameResult().getTeamResultAway().setConceded(game.getTeamAway().getId().equals(uploadGameCommand.getConcedingTeamId()));
      }
      SequenceGenerator.getInstance().pushEndGameSequence(gameState, true);
      gameState.findNextStep(null);
    }
  }

}
