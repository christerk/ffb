package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;




/**
 * 
 * @author Kalimar
 */
public class ReportPlayerAction implements IReport {

  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  private static final String _XML_ATTRIBUTE_PLAYER_ACTION = "playerAction";
  
  private String fActingPlayerId;
  private PlayerAction fPlayerAction;
  
  public ReportPlayerAction() {
    super();
  }

  public ReportPlayerAction(String pActingPlayerId, PlayerAction pPlayerAction) {
    this();
    fActingPlayerId = pActingPlayerId;
    fPlayerAction = pPlayerAction;
  }
  
  public ReportId getId() {
    return ReportId.PLAYER_ACTION;
  }
  
  public String getActingPlayerId() {
    return fActingPlayerId;
  }
  
  public PlayerAction getPlayerAction() {
    return fPlayerAction;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportPlayerAction(getActingPlayerId(), getPlayerAction());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getActingPlayerId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ACTION, (getPlayerAction() != null) ? getPlayerAction().getName() : null);
    UtilXml.addEmptyElement(pHandler, XML_TAG, attributes);
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getActingPlayerId());
    pByteList.addByte((byte)((getPlayerAction() != null) ? getPlayerAction().getId() : 0));
  }
  
  public int initFrom(ByteArray pByteArray) {
    ReportId reportId = ReportId.fromId(pByteArray.getSmallInt());
    if (getId() != reportId) {
      throw new IllegalStateException("Wrong report id. Expected " + getId().getName() + " received " + ((reportId != null) ? reportId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fActingPlayerId = pByteArray.getString();
    fPlayerAction = PlayerAction.fromId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }
    
}
