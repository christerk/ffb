package com.balancedbytes.games.ffb.net.commands;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ServerCommandTalk extends ServerCommand {
  
  private static final String _XML_ATTRIBUTE_COACH = "coach";
  private static final String _XML_TAG_TALK = "talk";
  
  private String fCoach;
  private List<String> fTalk;
  
  public ServerCommandTalk() {
    fTalk = new ArrayList<String>();
  }
  
  public ServerCommandTalk(String pCoach, String pTalk) {
    this();
    fCoach = pCoach;
    addTalk(pTalk);
  }
  
  public ServerCommandTalk(String pCoach, String[] pTalk) {
    this();
    fCoach = pCoach;
    addTalk(pTalk);
  }

  public NetCommandId getId() {
    return NetCommandId.SERVER_TALK;
  }
  
  public String getCoach() {
    return fCoach;
  }
  
  public void addTalk(String pTalk) {
    if (StringTool.isProvided(pTalk)) {
      fTalk.add(pTalk);
    }
  }
  
  public void addTalk(String[] pTalk) {
    if (ArrayTool.isProvided(pTalk)) {
      for (String talk : pTalk) {
        addTalk(talk);
      }
    }
  }

  public String[] getTalk() {
    return fTalk.toArray(new String[fTalk.size()]);
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
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_COACH, getCoach());
    UtilXml.startElement(pHandler, getId().getName(), attributes);
    String[] talkArray = getTalk();
    for (String talk : talkArray) {
      UtilXml.addValueElement(pHandler, _XML_TAG_TALK, talk);
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
    pByteList.addString(getCoach());
    pByteList.addStringArray(getTalk());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setCommandNr(pByteArray.getSmallInt());
    fCoach = pByteArray.getString();
    addTalk(pByteArray.getStringArray());
    return byteArraySerializationVersion;
  }
    
}
