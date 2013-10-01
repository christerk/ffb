package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.TeamList;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ServerCommandTeamList extends ServerCommand {
 
  private TeamList fTeamList;
  
  public ServerCommandTeamList() {
    super();
  }
  
  public ServerCommandTeamList(TeamList pTeamList) {
    this();
    fTeamList = pTeamList;
  }
  
  public NetCommandId getId() {
    return NetCommandId.SERVER_TEAM_LIST;
  }

  public TeamList getTeamList() {
    return fTeamList;
  }
  
  public boolean isReplayable() {
    return false;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    if (getCommandNr() > 0) {
      UtilXml.addAttribute(attributes, XML_ATTRIBUTE_COMMAND_NR, getCommandNr());
    }
    UtilXml.startElement(pHandler, getId().getName(), attributes);
    if (getTeamList() != null) {
      getTeamList().addToXml(pHandler);
    }
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
    pByteList.addSmallInt(getCommandNr());
    boolean hasTeamList = (getTeamList() != null);
    pByteList.addBoolean(hasTeamList);
    if (hasTeamList) {
      getTeamList().addTo(pByteList);
    }
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setCommandNr(pByteArray.getSmallInt());
    boolean hasTeamList = pByteArray.getBoolean();
    if (hasTeamList) {
      fTeamList = new TeamList();
      fTeamList.initFrom(pByteArray);
    }
    return byteArraySerializationVersion;
  }
    
}
