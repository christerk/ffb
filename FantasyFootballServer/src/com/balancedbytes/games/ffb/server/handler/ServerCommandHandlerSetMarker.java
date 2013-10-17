package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldMarker;
import com.balancedbytes.games.ffb.PlayerMarker;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ClientCommandSetMarker;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.old.IDbTableFieldModels;
import com.balancedbytes.games.ffb.server.net.ChannelManager;
import com.balancedbytes.games.ffb.server.util.UtilGame;
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

  public void handleNetCommand(NetCommand pNetCommand) {

    ClientCommandSetMarker setMarkerCommand = (ClientCommandSetMarker) pNetCommand;
    
    ChannelManager channelManager = getServer().getChannelManager();
    long gameId = channelManager.getGameIdForChannel(setMarkerCommand.getSender());
    GameState gameState = getServer().getGameCache().getGameStateById(gameId);
    boolean homeMarker = (setMarkerCommand.getSender() == channelManager.getChannelOfHomeCoach(gameState));
    boolean awayMarker = (setMarkerCommand.getSender() == channelManager.getChannelOfAwayCoach(gameState));

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
      
      UtilGame.syncGameModel(gameState, null, null, null);
      
    }
    
  }
      
}
