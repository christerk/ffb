package com.balancedbytes.games.ffb.client.handler;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.GameTitle;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ServerCommandJoin;
import com.balancedbytes.games.ffb.util.ArrayTool;

public class ClientCommandHandlerJoin extends ClientCommandHandler {

  protected ClientCommandHandlerJoin(FantasyFootballClient pClient) {
    super(pClient);
  }

  public NetCommandId getId() {
    return NetCommandId.SERVER_JOIN;
  }

  public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {
    
    if (pMode == ClientCommandHandlerMode.QUEUING) {
      return true;
    }
    
    ServerCommandJoin joinCommand = (ServerCommandJoin) pNetCommand;
    UserInterface userInterface = getClient().getUserInterface();
    
    if (ClientMode.PLAYER == joinCommand.getMode()) {
      getClient().getClientData().setTurnTimerStopped(false);
    }
    
    String[] players = joinCommand.getPlayers();
    if (ArrayTool.isProvided(players) && (players.length > 1)) {
      
      String homeCoach = null;
      String awayCoach = null;
      if (players[1].equals(getClient().getParameters().getCoach())) {
        homeCoach = players[1];
        awayCoach = players[0];
      } else {
        homeCoach = players[0];
        awayCoach = players[1];
      }

      GameTitle gameTitle = userInterface.getGameTitle();
      gameTitle.setClientMode(getClient().getMode());
      gameTitle.setHomeCoach(homeCoach);
      gameTitle.setAwayCoach(awayCoach);
      updateGameTitle(gameTitle);  

    }

    getClient().getClientData().setSpectators(joinCommand.getSpectators());
    
    if (pMode != ClientCommandHandlerMode.REPLAYING) {
      if (ClientMode.PLAYER == joinCommand.getMode()) {
        userInterface.getLog().markCommandBegin(joinCommand.getCommandNr());
        userInterface.getStatusReport().reportJoin(joinCommand);
        userInterface.getLog().markCommandEnd(joinCommand.getCommandNr());
      }
      refreshSideBars();
    }
    
    return true;
    
  }
  
}
