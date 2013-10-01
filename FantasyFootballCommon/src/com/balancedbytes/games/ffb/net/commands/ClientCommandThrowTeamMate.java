package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ClientCommandThrowTeamMate extends NetCommand implements ICommandWithActingPlayer {
   
  private static final String _XML_ATTRIBUTE_X = "x";
  private static final String _XML_ATTRIBUTE_Y = "y";
  private static final String _XML_ATTRIBUTE_THROWN_PLAYER_ID = "thrownPlayerId";
  private static final String _XML_ATTRIBUTE_ACTING_PLAYER_ID = "actingPlayerId";

  private static final String _XML_TAG_TARGET_COORDINATE = "targetCoordinate";
  
  private FieldCoordinate fTargetCoordinate;
  private String fThrownPlayerId;
  private String fActingPlayerId;
  
  public ClientCommandThrowTeamMate() {
    super();
  }

  public ClientCommandThrowTeamMate(String pActingPlayerId, String pThrownPlayerId) {
  	fActingPlayerId = pActingPlayerId;
    fThrownPlayerId = pThrownPlayerId;
    fTargetCoordinate = null;
  }
  
  public ClientCommandThrowTeamMate(String pActingPlayerId, FieldCoordinate pTargetCoordinate) {
  	fActingPlayerId = pActingPlayerId;
    fTargetCoordinate = pTargetCoordinate;
    fThrownPlayerId = null;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_THROW_TEAM_MATE;
  }
  
  public String getActingPlayerId() {
    return fActingPlayerId;
  }

  public String getThrownPlayerId() {
    return fThrownPlayerId;
  }
  
  public FieldCoordinate getTargetCoordinate() {
    return fTargetCoordinate;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ACTING_PLAYER_ID, getActingPlayerId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_THROWN_PLAYER_ID, getThrownPlayerId());
    UtilXml.startElement(pHandler, getId().getName(), attributes);
    if (getTargetCoordinate() != null) {
      attributes = new AttributesImpl();
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_X, getTargetCoordinate().getX());
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_Y, getTargetCoordinate().getY());
      UtilXml.addEmptyElement(pHandler, _XML_TAG_TARGET_COORDINATE, attributes);
    }
    UtilXml.endElement(pHandler, getId().getName());
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 2;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getActingPlayerId());
    pByteList.addString(getThrownPlayerId());
    pByteList.addFieldCoordinate(getTargetCoordinate());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    if (byteArraySerializationVersion > 1) {
    	fActingPlayerId = pByteArray.getString();
    }
    fThrownPlayerId = pByteArray.getString();
    fTargetCoordinate = pByteArray.getFieldCoordinate();
    return byteArraySerializationVersion;
  }

}
