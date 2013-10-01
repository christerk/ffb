package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;




/**
 * 
 * @author Kalimar
 */
public class ReportApothecaryRoll implements IReport {
  
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  private static final String _XML_ATTRIBUTE_CASUALTY_ROLL = "casualtyRoll";
  private static final String _XML_ATTRIBUTE_PLAYER_STATE = "playerState";
  private static final String _XML_ATTRIBUTE_SERIOUS_INJURY = "seriousInjury";

  private String fPlayerId;
  private int[] fCasualtyRoll;
  private PlayerState fPlayerState;
  private SeriousInjury fSeriousInjury;

  public ReportApothecaryRoll() {
    super();
  }

  public ReportApothecaryRoll(String pPlayerId, int[] pCasualtyRoll, PlayerState pPlayerState, SeriousInjury pSeriousInjury) {
    fPlayerId = pPlayerId;
    fCasualtyRoll = pCasualtyRoll;
    fPlayerState = pPlayerState;
    fSeriousInjury = pSeriousInjury;
  }
  
  public ReportId getId() {
    return ReportId.APOTHECARY_ROLL;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }

  public int[] getCasualtyRoll() {
    return fCasualtyRoll;
  }

  public PlayerState getPlayerState() {
    return fPlayerState;
  }

  public SeriousInjury getSeriousInjury() {
    return fSeriousInjury;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportApothecaryRoll(getPlayerId(), getCasualtyRoll(), getPlayerState(), getSeriousInjury());
  }

  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CASUALTY_ROLL, getCasualtyRoll());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_STATE, (getPlayerState() != null) ? getPlayerState().getId() : 0);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SERIOUS_INJURY, (getSeriousInjury() != null) ? getSeriousInjury().getName() : null);
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
    pByteList.addString(getPlayerId());
    pByteList.addByteArray(getCasualtyRoll());
    pByteList.addSmallInt((getPlayerState() != null) ? getPlayerState().getId() : 0);
    pByteList.addByte((byte) ((getSeriousInjury() != null) ? getSeriousInjury().getId() : 0));
  }

  public int initFrom(ByteArray pByteArray) {
    ReportId reportId = ReportId.fromId(pByteArray.getSmallInt());
    if (getId() != reportId) {
      throw new IllegalStateException("Wrong report id. Expected " + getId().getName() + " received " + ((reportId != null) ? reportId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fCasualtyRoll = pByteArray.getByteArrayAsIntArray();
    fPlayerState = new PlayerState(pByteArray.getSmallInt());
    fSeriousInjury = SeriousInjury.fromId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }
    
}
