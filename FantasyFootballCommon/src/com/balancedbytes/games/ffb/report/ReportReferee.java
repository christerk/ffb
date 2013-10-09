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
public class ReportReferee implements IReport {
  
  private static final String _XML_ATTRIBUTE_FOULING_PLAYER_BANNED = "foulingPlayerBanned";
  
  private boolean fFoulingPlayerBanned;
  
  public ReportReferee() {
    super();
  }
  
  public ReportReferee(boolean pFoulingPlayerBanned) {
    fFoulingPlayerBanned = pFoulingPlayerBanned;
  }
  
  public ReportId getId() {
    return ReportId.REFEREE;
  }
  
  public boolean isFoulingPlayerBanned() {
    return fFoulingPlayerBanned;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportReferee(isFoulingPlayerBanned());
  }

  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_FOULING_PLAYER_BANNED, isFoulingPlayerBanned());
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
    pByteList.addBoolean(isFoulingPlayerBanned());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fFoulingPlayerBanned = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
  
}
