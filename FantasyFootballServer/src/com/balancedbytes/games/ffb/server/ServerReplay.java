package com.balancedbytes.games.ffb.server;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.net.commands.ServerCommand;

/**
 * 
 * @author Kalimar
 */
public class ServerReplay {
  
  private GameState fGameState;
  private int fFromCommandNr;
  private int fToCommandNr;
  private SocketChannel fReceiver;
  private boolean fComplete;
  private int fTotalNrOfCommands;
  
  public ServerReplay(GameState pGameState, int pToCommandNr, SocketChannel pReceiver) {
    fGameState = pGameState;
    fToCommandNr = pToCommandNr;
    fReceiver = pReceiver;
  }
  
  public void setFromCommandNr(int pFromCommandNr) {
    fFromCommandNr = pFromCommandNr;
  }
  
  public int getFromCommandNr() {
    return fFromCommandNr;
  }
  
  public int getToCommandNr() {
    return fToCommandNr;
  }
  
  public GameState getGameState() {
    return fGameState;
  }
  
  public SocketChannel getReceiver() {
    return fReceiver;
  }
  
  public void setComplete(boolean pComplete) {
    fComplete = pComplete;
  }
  
  public boolean isComplete() {
    return fComplete;
  }

  public ServerCommand[] findRelevantCommandsInLog() {
  	List<ServerCommand> replayCommands = new ArrayList<ServerCommand>();
  	ServerCommand[] logCommands = getGameState().getGameLog().getServerCommands();
  	for (ServerCommand serverCommand : logCommands) {
  		if ((serverCommand.getCommandNr() >= getFromCommandNr()) && (serverCommand.getCommandNr() < getToCommandNr())) {
  			replayCommands.add(serverCommand);
  		}
  	}
    return replayCommands.toArray(new ServerCommand[replayCommands.size()]);
  }
  
  public void setTotalNrOfCommands(int pTotalNrOfCommands) {
    fTotalNrOfCommands = pTotalNrOfCommands;
  }
  
  public int getTotalNrOfCommands() {
    return fTotalNrOfCommands;
  }

}
