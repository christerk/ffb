package com.balancedbytes.games.ffb.server.util;

import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.ChannelManager;

/**
 * 
 * @author Kalimar
 */
public class UtilCommand {
  
  public static boolean isHomeCommand(FantasyFootballServer pServer, NetCommand pNetCommand) {
  
    if ((pServer != null) && (pNetCommand != null)) {
      ChannelManager channelManager = pServer.getChannelManager();
      long gameId = channelManager.getGameIdForChannel(pNetCommand.getSender());
      GameState gameState = pServer.getGameCache().getGameStateById(gameId);
      return (pServer.getChannelManager().getChannelOfHomeCoach(gameState) == pNetCommand.getSender());
    } else {
      return false;
    }
    
  }

}
