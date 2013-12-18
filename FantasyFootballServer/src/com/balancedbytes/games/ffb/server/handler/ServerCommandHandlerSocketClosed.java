package com.balancedbytes.games.ffb.server.handler;

import java.nio.channels.SocketChannel;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameCache;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.ServerMode;
import com.balancedbytes.games.ffb.server.fumbbl.FumbblRequestRemoveGamestate;
import com.balancedbytes.games.ffb.server.net.ChannelManager;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.util.UtilTimer;
import com.balancedbytes.games.ffb.util.ArrayTool;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerSocketClosed extends ServerCommandHandler {

  protected ServerCommandHandlerSocketClosed(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_SOCKET_CLOSED;
  }

  public void handleCommand(ReceivedCommand pReceivedCommand) {
    
    SocketChannel sender = pReceivedCommand.getSender();
    
    ChannelManager channelManager = getServer().getChannelManager();
    String coach = channelManager.getCoachForChannel(sender);
    ClientMode mode = channelManager.getModeForChannel(sender);
    long gameId = channelManager.getGameIdForChannel(sender);
    channelManager.removeChannel(sender);

    SocketChannel[] receivers = channelManager.getChannelsForGameId(gameId);

    GameCache gameCache = getServer().getGameCache();
    GameState gameState = gameCache.getGameStateById(gameId);
    if (gameState != null) {
      
      int spectators = 0;
      for (int i = 0; i < receivers.length; i++) {
        if (channelManager.getModeForChannel(receivers[i]) == ClientMode.SPECTATOR) {
          spectators++;
        }
      }
      
      // stop timer whenever a player drops out
    	if (ClientMode.PLAYER == mode) {
      	UtilTimer.syncTime(gameState);
        UtilTimer.stopTurnTimer(gameState);
    	}
      
      SocketChannel homeChannel = channelManager.getChannelOfHomeCoach(gameState);
      SocketChannel awayChannel = channelManager.getChannelOfAwayCoach(gameState);

      if ((homeChannel == null) && (awayChannel == null) && ((GameStatus.STARTING == gameState.getStatus()) || (GameStatus.ACTIVE == gameState.getStatus()))) {
        gameState.setStatus(GameStatus.PAUSED);
        gameCache.queueDbUpdate(gameState);
        removeFumbblGame(gameState);
        gameState.fetchChanges();  // remove all changes from queue
      }

    	if (ArrayTool.isProvided(receivers)) {
    		getServer().getCommunication().sendLeave(receivers, coach, mode, spectators);
    	} else {
        getServer().getGameCache().removeGameStateFromCache(gameState);
    	}

    }
    
  }
  
  private void removeFumbblGame(GameState pGameState) {
    if (!pGameState.getGame().isTesting() && (getServer().getMode() == ServerMode.FUMBBL)) {
      getServer().getFumbblRequestProcessor().add(new FumbblRequestRemoveGamestate(pGameState));
    }
  }

}
