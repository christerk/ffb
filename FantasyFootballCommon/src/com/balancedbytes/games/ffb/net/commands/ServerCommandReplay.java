package com.balancedbytes.games.ffb.net.commands;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommandFactory;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandReplay extends ServerCommand {
  
  private static final String _XML_ATTRIBUTE_NR_OF_COMMANDS = "nrOfCommands";
  private static final String _XML_ATTRIBUTE_TOTAL_NR_OF_COMMANDS = "totalNrOfCommands";
  
  public static final int SIZE_LIMIT = (8 * 1024) - 10; 
  
  private List<ServerCommand> fReplayCommands;
  private int fTotalNrOfCommands;
  
  private transient SocketChannel fReceiver;

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
  
  public void setReceiver(SocketChannel pReceiver) {
    fReceiver = pReceiver;
  }
  
  public SocketChannel getReceiver() {
    return fReceiver;
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
  
  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NR_OF_COMMANDS, getNrOfCommands());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TOTAL_NR_OF_COMMANDS, getTotalNrOfCommands());
    UtilXml.startElement(pHandler, getId().getName(), attributes);
    // to cut this short in the debug log ...
//    ServerCommand[] replayCommands = getReplayCommands();
//    for (ServerCommand replayCommand : replayCommands) {
//      replayCommand.addToXml(pHandler);
//    }
    UtilXml.endElement(pHandler, getId().getName());
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addSmallInt(getNrOfCommands());
    pByteList.addSmallInt(getTotalNrOfCommands());
    ServerCommand[] replayCommands = getReplayCommands();
    for (ServerCommand replayCommand : replayCommands) {
      byte[] commandBytes = replayCommand.toBytes();
      for (int i = 0; i < commandBytes.length; i++) {
        pByteList.addByte(commandBytes[i]);
      }
    }
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    int nrOfCommands = pByteArray.getSmallInt();
    fTotalNrOfCommands = pByteArray.getSmallInt();
    initFrom(pByteArray, nrOfCommands);
    return byteArraySerializationVersion;
  }

  public void initFrom(ByteArray pByteArray, int pNrOfCommands) {
    for (int i = 0; i < pNrOfCommands; i++) {
      byte[] commandBytes = new byte[pByteArray.getSmallInt(pByteArray.getPosition() + 2)];
      for (int j = 0; j < commandBytes.length; j++) {
        commandBytes[j] = pByteArray.getByte();
      }
      ServerCommand replayCommand = (ServerCommand) NetCommandFactory.getInstance().fromBytes(commandBytes);
//      System.out.println("[" + (i + 1) + "] " + replayCommand.getId().getName());
      add(replayCommand);
    }
  }
  
}
