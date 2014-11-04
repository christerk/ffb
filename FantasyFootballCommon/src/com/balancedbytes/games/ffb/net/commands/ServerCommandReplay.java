package com.balancedbytes.games.ffb.net.commands;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandFactory;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandReplay extends ServerCommand {
  
  public static final int MAX_NR_OF_COMMANDS = 50; 
  
  private List<ServerCommand> fReplayCommands;
  private int fTotalNrOfCommands;
  
  public NetCommandId getId() {
    return NetCommandId.SERVER_REPLAY;
  }
  
  public ServerCommandReplay() {
    fReplayCommands = new ArrayList<ServerCommand>();
  }
      
  public void add(ServerCommand pServerCommand) {
    if (pServerCommand != null) {
      fReplayCommands.add(pServerCommand);
    }
  }
  
  public void add(ServerCommand[] pServerCommands) {
    if (ArrayTool.isProvided(pServerCommands)) {
      for (ServerCommand serverCommand : pServerCommands) {
        add(serverCommand);
      }
    }
  }
  
  public int getNrOfCommands() {
    return fReplayCommands.size();
  }
    
  public void setTotalNrOfCommands(int pTotalNrOfCommands) {
    fTotalNrOfCommands = pTotalNrOfCommands;
  }
  
  public int getTotalNrOfCommands() {
    return fTotalNrOfCommands;
  }
  
  public ServerCommand[] getReplayCommands() {
    return fReplayCommands.toArray(new ServerCommand[fReplayCommands.size()]);
  }
  
  public boolean isReplayable() {
    return false;
  }
  
  public int findHighestCommandNr() {
    int highestCommandNr = 0;
    for (ServerCommand serverCommand : fReplayCommands) {
      if (serverCommand.getCommandNr() > highestCommandNr) {
        highestCommandNr = serverCommand.getCommandNr();
      }
    }
    return highestCommandNr;
  }
  
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    int nrOfCommands = pByteArray.getSmallInt();
    fTotalNrOfCommands = pByteArray.getSmallInt();
    initFrom(pByteArray, nrOfCommands);
    return byteArraySerializationVersion;
  }

  public void initFrom(ByteArray pByteArray, int pNrOfCommands) {
    NetCommandFactory netCommandFactory = new NetCommandFactory();
    for (int i = 0; i < pNrOfCommands; i++) {
      byte[] commandBytes = new byte[pByteArray.getSmallInt(pByteArray.getPosition() + 2)];
      for (int j = 0; j < commandBytes.length; j++) {
        commandBytes[j] = pByteArray.getByte();
      }
      ServerCommand replayCommand = (ServerCommand) netCommandFactory.fromBytes(commandBytes);
//      System.out.println("[" + (i + 1) + "] " + replayCommand.getId().getName());
      add(replayCommand);
    }
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
    IJsonOption.TOTAL_NR_OF_COMMANDS.addTo(jsonObject, fTotalNrOfCommands);
    JsonArray commandArray = new JsonArray();
    for (ServerCommand replayCommand : getReplayCommands()) {
      commandArray.add(replayCommand.toJsonValue());
    }
    IJsonOption.COMMAND_ARRAY.addTo(jsonObject, commandArray);
    return jsonObject;
  }
  
  public ServerCommandReplay initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fTotalNrOfCommands = IJsonOption.TOTAL_NR_OF_COMMANDS.getFrom(jsonObject);
    JsonArray commandArray = IJsonOption.COMMAND_ARRAY.getFrom(jsonObject);
    fReplayCommands.clear();
    NetCommandFactory netCommandFactory = new NetCommandFactory();
    for (int i = 0; i < commandArray.size(); i++) {
      ServerCommand replayCommand = (ServerCommand) netCommandFactory.forJsonValue(commandArray.get(i));
      add(replayCommand);
    }
    return this;
  }
  
}
