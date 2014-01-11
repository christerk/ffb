package com.balancedbytes.games.ffb.server;

import java.util.LinkedList;
import java.util.List;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.commands.ServerCommand;
import com.balancedbytes.games.ffb.net.commands.ServerCommandReplay;

/**
 * 
 * @author Kalimar
 */
public class ServerReplayer implements Runnable {
  
  private boolean fStopped;
  private List<ServerReplay> fReplayQueue;
  private FantasyFootballServer fServer;
    
  public ServerReplayer(FantasyFootballServer pServer) {
    fServer = pServer;
    fReplayQueue = new LinkedList<ServerReplay>();
  }
  
  public void add(ServerReplay pReplay) {
    synchronized (fReplayQueue) {
      fReplayQueue.add(pReplay);
      fReplayQueue.notify();
    }
  }
  
  public void run() {
    
    ServerReplay serverReplay = null;

    try {
    
      while (true) {
        
      	synchronized (fReplayQueue) {
          try {
            while (fReplayQueue.isEmpty() && !fStopped) {
              fReplayQueue.wait();
            }
          } catch (InterruptedException e) {
            break;
          }
          if (fStopped) {
            break;
          }
          if ((serverReplay == null) && !fReplayQueue.isEmpty()) {
            serverReplay = fReplayQueue.remove(0);
            GameState gameState = serverReplay.getGameState();
            serverReplay.setTotalNrOfCommands(gameState.getGameLog().size());
          }
        }
        
      	while (serverReplay != null) {
          
      		int nrOfCommands = 0;
          ByteList replayByteList = new ByteList();
        	
          serverReplay.setComplete(true);
          ServerCommand[] serverCommands = serverReplay.findRelevantCommandsInLog();
        	for (ServerCommand serverCommand : serverCommands) {
            byte[] commandBytes = serverCommand.toBytes();
            if (replayByteList.size() + commandBytes.length <= ServerCommandReplay.SIZE_LIMIT) {
            	nrOfCommands++;
            	for (byte commandByte : commandBytes) {
                replayByteList.addByte(commandByte);
            	}
            } else {
              serverReplay.setComplete(false);
              break;
            }
        	}
          
        	ServerCommandReplay replayCommand = new ServerCommandReplay();
          replayCommand.initFrom(new ByteArray(replayByteList.toBytes()), nrOfCommands);
          replayCommand.setTotalNrOfCommands(serverReplay.getTotalNrOfCommands());
          getServer().getCommunication().send(serverReplay.getSession(), replayCommand, false);
          getServer().getDebugLog().logServerCommand(IServerLogLevel.DEBUG, (serverReplay.getGameState() != null) ? serverReplay.getGameState().getId() : -1, replayCommand, DebugLog.COMMAND_SERVER_SPECTATOR);
          if (!serverReplay.isComplete()) {
            serverReplay.setFromCommandNr(replayCommand.findHighestCommandNr() + 1);
          } else {
          	serverReplay = null;
          }
          
        }
      	
      }
      
    } catch (Exception pException) {
    	GameState gameState = (serverReplay != null) ? serverReplay.getGameState() : null;
      getServer().getDebugLog().log((gameState != null) ? gameState.getId() : -1, pException);
      System.exit(99);
    }
    
  }
  
  public void stop() {
    fStopped = true;
    synchronized (fReplayQueue) {
      fReplayQueue.notifyAll();
    }
  }
  
  public FantasyFootballServer getServer() {
    return fServer;
  }
    
}
