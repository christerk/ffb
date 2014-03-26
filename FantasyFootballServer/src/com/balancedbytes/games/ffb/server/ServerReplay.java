package com.balancedbytes.games.ffb.server;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.websocket.api.Session;

import com.balancedbytes.games.ffb.net.commands.ServerCommand;

/**
 * 
 * @author Kalimar
 */
public class ServerReplay {
  
  private GameState fGameState;
  private int fFromCommandNr;
  private int fToCommandNr;
  private Session fSession;
  private boolean fComplete;
  private int fTotalNrOfCommands;
  
  public ServerReplay(GameState pGameState, int pToCommandNr, Session pSession) {
    fGameState = pGameState;
    fToCommandNr = pToCommandNr;
    fSession = pSession;
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
  
  public Session getSession() {
    return fSession;
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

  public int findTotalNrOfCommands() {
    int nrOfCommands = 0;
    ServerCommand[] logCommands = getGameState().getGameLog().getServerCommands();
    for (ServerCommand serverCommand : logCommands) {
      if ((serverCommand.getCommandNr() >= getFromCommandNr()) && (serverCommand.getCommandNr() < getToCommandNr())) {
        nrOfCommands++;
      }
    }
    return nrOfCommands;
  }

  public void setTotalNrOfCommands(int pTotalNrOfCommands) {
    fTotalNrOfCommands = pTotalNrOfCommands;
  }
  
  public int getTotalNrOfCommands() {
    return fTotalNrOfCommands;
  }

}
