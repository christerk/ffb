package com.balancedbytes.games.ffb.client.handler;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.GameTitle;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ServerCommandGameTime;

public class ClientCommandHandlerGameTime extends ClientCommandHandler {

  protected ClientCommandHandlerGameTime(FantasyFootballClient pClient) {
    super(pClient);
  }

  public NetCommandId getId() {
    return NetCommandId.SERVER_GAME_TIME;
  }

  public boolean handleNetCommand(NetCommand netCommand, ClientCommandHandlerMode mode) {
    
    ServerCommandGameTime gameTimeCommand = (ServerCommandGameTime) netCommand;
    
    // System.out.println(StringTool.formatThousands(gameTimeCommand.getGameTime()) + " ms");
    
    UserInterface userInterface = getClient().getUserInterface();
    GameTitle gameTitle = new GameTitle(userInterface.getGameTitle());
    gameTitle.setGameTime(gameTimeCommand.getGameTime());
    gameTitle.setTurnTime(gameTimeCommand.getTurnTime());
    updateGameTitle(gameTitle);
    
    return true;

  }
  
}
