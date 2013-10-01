package com.balancedbytes.games.ffb.server;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.net.NetCommandFactory;
import com.balancedbytes.games.ffb.net.commands.ServerCommand;

/**
 * 
 * @author Kalimar
 */
public class GameLog implements IByteArraySerializable {

  private List<ServerCommand> fServerCommands;

  private transient int fLastCommitedCommandNr;  // TODO: can be removed
  private transient GameState fGameState;
    
  public GameLog(GameState pGameState) {
    fGameState = pGameState;
    fServerCommands = new ArrayList<ServerCommand>();
  }
  
  public void add(ServerCommand pServerCommand) {
    if (pServerCommand != null) {
      synchronized (fServerCommands) {
        if (pServerCommand.isReplayable()) {
          fServerCommands.add(pServerCommand);
        }
      }
    }
  }
  
  public ServerCommand[] getServerCommands() {
    synchronized (fServerCommands) {
      return fServerCommands.toArray(new ServerCommand[fServerCommands.size()]);
    }
  }

  public ServerCommand[] getUncommitedServerCommands() {
		List<ServerCommand> uncommitedCommands = new ArrayList<ServerCommand>();
  	synchronized (fServerCommands) {
  		for (ServerCommand serverCommand : fServerCommands) {
  			if (serverCommand.getCommandNr() > fLastCommitedCommandNr) {
  				uncommitedCommands.add(serverCommand);
  			}
  		}
    }
    return uncommitedCommands.toArray(new ServerCommand[uncommitedCommands.size()]);
  }
  
  public int findMaxCommandNr() {
  	int maxCommandNr = 0;
  	synchronized (fServerCommands) {
  		for (ServerCommand serverCommand : fServerCommands) {
  			if (serverCommand.getCommandNr() > maxCommandNr) {
  				maxCommandNr = serverCommand.getCommandNr();
  			}
  		}
  	}
  	return maxCommandNr;
  }

  public void setLastCommitedCommandNr(int pLastCommitedCommandNr) {
    synchronized (fServerCommands) {
    	fLastCommitedCommandNr = pLastCommitedCommandNr;
    }
  }
  
  public int getLastCommitedCommandNr() {
    synchronized (fServerCommands) {
    	return fLastCommitedCommandNr;
    }
  }
  
  public void clear() {
    synchronized (fServerCommands) {
      fServerCommands.clear();
    }
  }
  
  public int size() {
    synchronized (fServerCommands) {
      return fServerCommands.size();
    }
  }
    
  public GameState getGameState() {
    return fGameState;
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addSmallInt(size());
    for (ServerCommand serverCommand : getServerCommands()) {
    	serverCommand.addTo(pByteList);
    }
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fServerCommands.clear();
    int nrOfCommands = pByteArray.getSmallInt();
    for (int i = 0; i < nrOfCommands; i++) {
    	fServerCommands.add(initCommandFrom(pByteArray));
    }
    return byteArraySerializationVersion;
  }
  
  private ServerCommand initCommandFrom(ByteArray pByteArray) {
    byte[] commandBytes = new byte[pByteArray.getSmallInt(pByteArray.getPosition() + 2)];
    for (int j = 0; j < commandBytes.length; j++) {
      commandBytes[j] = pByteArray.getByte();
    }
    return (ServerCommand) NetCommandFactory.getInstance().fromBytes(commandBytes);
  }
  
}
