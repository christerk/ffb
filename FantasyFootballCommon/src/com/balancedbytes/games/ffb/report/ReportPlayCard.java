package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ReportPlayCard implements IReport {
  
  private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
  private static final String _XML_ATTRIBUTE_CARD = "card";
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";

  private String fTeamId;
  private Card fCard;
  private String fPlayerId;
  
  public ReportPlayCard() {
    super();
  }

  public ReportPlayCard(String pTeamId, Card pCard) {
  	fTeamId = pTeamId;
  	fCard = pCard;
  }
  
  public ReportPlayCard(String pTeamId, Card pCard, String pCatcherId) {
  	this(pTeamId, pCard);
    fPlayerId = pCatcherId;
  }
  
  public ReportId getId() {
    return ReportId.PLAY_CARD;
  }
  
  public String getTeamId() {
	  return fTeamId;
  }
  
  public Card getCard() {
	  return fCard;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }

  // transformation
  
  public IReport transform() {
    return new ReportPlayCard(getTeamId(), getCard(), getPlayerId());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CARD, (getCard() != null) ? getCard().getName() : null);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
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
    pByteList.addString(getTeamId());
    pByteList.addSmallInt((getCard() != null) ? getCard().getId() : 0);
    pByteList.addString(getPlayerId());
  }
  
  public int initFrom(ByteArray pByteArray) {
    ReportId reportId = ReportId.fromId(pByteArray.getSmallInt());
    if (getId() != reportId) {
      throw new IllegalStateException("Wrong report id. Expected " + getId().getName() + " received " + ((reportId != null) ? reportId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fTeamId = pByteArray.getString();
    fCard = new CardFactory().forId(pByteArray.getSmallInt());
    fPlayerId = pByteArray.getString();
    return byteArraySerializationVersion;
  }
    
}
