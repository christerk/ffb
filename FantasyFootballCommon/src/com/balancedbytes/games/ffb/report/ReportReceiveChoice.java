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
public class ReportReceiveChoice implements IReport {
  
  private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
  private static final String _XML_ATTRIBUTE_CHOICE_RECEIVE = "choiceReceive";
  
  private String fTeamId;
  private boolean fChoiceReceive;
  
  public ReportReceiveChoice() {
    super();
  }

  public ReportReceiveChoice(String pTeamId, boolean pChoiceReceive) {
    fTeamId = pTeamId;
    fChoiceReceive = pChoiceReceive;
  }
  
  public ReportId getId() {
    return ReportId.RECEIVE_CHOICE;
  }
  
  public String getTeamId() {
    return fTeamId;
  }
  
  public boolean isChoiceReceive() {
    return fChoiceReceive;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportReceiveChoice(getTeamId(), isChoiceReceive());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CHOICE_RECEIVE, isChoiceReceive());
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
    pByteList.addBoolean(isChoiceReceive());
  }

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fTeamId = pByteArray.getString();
    fChoiceReceive = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
    
}
