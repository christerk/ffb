package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;




/**
 * 
 * @author Kalimar
 */
public class ReportDoubleHiredStarPlayer implements IReport {
  
  private static final String _XML_ATTRIBUTE_STAR_PLAYER_NAME = "starPlayerName";
  
  private String fStarPlayerName;
  
  public ReportDoubleHiredStarPlayer() {
    super();
  }
  
  public ReportDoubleHiredStarPlayer(String pStarPlayerName) {
    fStarPlayerName = pStarPlayerName;
  }

  public ReportId getId() {
    return ReportId.DOUBLE_HIRED_STAR_PLAYER;
  }
  
  public String getStarPlayerName() {
    return fStarPlayerName;
  }

  // transformation
  
  public IReport transform() {
    return new ReportDoubleHiredStarPlayer(getStarPlayerName());
  }

  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_STAR_PLAYER_NAME, getStarPlayerName());
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
    pByteList.addString(getStarPlayerName());
  }
  
  public int initFrom(ByteArray pByteArray) {
    ReportId reportId = ReportId.fromId(pByteArray.getSmallInt());
    if (getId() != reportId) {
      throw new IllegalStateException("Wrong report id. Expected " + getId().getName() + " received " + ((reportId != null) ? reportId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fStarPlayerName = pByteArray.getString();
    return byteArraySerializationVersion;
  }
    
}
