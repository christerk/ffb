package com.balancedbytes.games.ffb.server;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.IByteArrayReadable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandFactory;
import com.balancedbytes.games.ffb.net.commands.ServerCommand;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class GameLog implements IByteArrayReadable, IJsonSerializable {

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
    return (ServerCommand) new NetCommandFactory().fromBytes(commandBytes);
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    JsonArray commandArray = new JsonArray();
    for (ServerCommand serverCommand : getServerCommands()) {
      commandArray.add(serverCommand.toJsonValue());
    }
    IJsonOption.COMMAND_ARRAY.addTo(jsonObject, commandArray);
    return jsonObject;
  }
  
  public GameLog initFrom(JsonValue pJsonValue) {
    NetCommandFactory netCommandFactory = new NetCommandFactory();
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    JsonArray commandArray = IJsonOption.COMMAND_ARRAY.getFrom(jsonObject);
    fServerCommands.clear();
    for (int i = 0; i < commandArray.size(); i++) {
      ServerCommand serverCommand = (ServerCommand) netCommandFactory.forJsonValue(commandArray.get(i));
      add(serverCommand);
    }
    return this;
  }
  
}
