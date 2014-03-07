package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldMarker;
import com.balancedbytes.games.ffb.PlayerMarker;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ClientCommandSetMarker;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.old.IDbTableFieldModels;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.SessionManager;
import com.balancedbytes.games.ffb.server.util.UtilServerGame;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerSetMarker extends ServerCommandHandler {
  
  protected ServerCommandHandlerSetMarker(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_SET_MARKER;
  }

  public void handleCommand(ReceivedCommand pReceivedCommand) {

    ClientCommandSetMarker setMarkerCommand = (ClientCommandSetMarker) pReceivedCommand.getCommand();
    
    SessionManager sessionManager = getServer().getSessionManager();
    long gameId = sessionManager.getGameIdForSession(pReceivedCommand.getSession());
    GameState gameState = getServer().getGameCache().getGameStateById(gameId);
    boolean homeMarker = (sessionManager.getSessionOfHomeCoach(gameState) == pReceivedCommand.getSession());
    boolean awayMarker = (sessionManager.getSessionOfAwayCoach(gameState) == pReceivedCommand.getSession());

    if (homeMarker || awayMarker) {
      
      Game game = gameState.getGame();    
      String text = setMarkerCommand.getText();
      if ((text != null) && (text.length() > IDbTableFieldModels.MAX_TEXT_LENGTH)) {
        text = text.substring(0, IDbTableFieldModels.MAX_TEXT_LENGTH);
      }
      FieldCoordinate coordinate = setMarkerCommand.getCoordinate();
      if ((coordinate != null) && !homeMarker) {
        coordinate = coordinate.transform();
      }
      
      if (setMarkerCommand.getCoordinate() != null) {
        FieldMarker fieldMarker = game.getFieldModel().getFieldMarker(coordinate);      
        if (fieldMarker == null) {
          fieldMarker = new FieldMarker(coordinate);
        }
        if (homeMarker) {
          fieldMarker.setHomeText(text);
        } else {
          fieldMarker.setAwayText(text);
        }
        if (StringTool.isProvided(fieldMarker.getHomeText()) || StringTool.isProvided(fieldMarker.getAwayText())) {
          game.getFieldModel().add(fieldMarker);
        } else {
          game.getFieldModel().remove(fieldMarker);
        }
        
      } else {
        PlayerMarker playerMarker = game.getFieldModel().getPlayerMarker(setMarkerCommand.getPlayerId());
        if (playerMarker == null) {
          playerMarker = new PlayerMarker(setMarkerCommand.getPlayerId());
        }
        if (homeMarker) {
          playerMarker.setHomeText(text);
        } else {
          playerMarker.setAwayText(text);
        }
        if (StringTool.isProvided(playerMarker.getHomeText()) || StringTool.isProvided(playerMarker.getAwayText())) {
          game.getFieldModel().add(playerMarker);
        } else {
          game.getFieldModel().remove(playerMarker);
        }
      }
      
      UtilServerGame.syncGameModel(gameState, null, null, null);
      
    }
    
  }
      
}
