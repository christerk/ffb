package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ReportTentaclesShadowingRoll implements IReport {
  
  private static final String _XML_ATTRIBUTE_SKILL = "skill";
  private static final String _XML_ATTRIBUTE_DEFENDER_ID = "defenderId";
  private static final String _XML_ATTRIBUTE_ROLL = "roll";
  private static final String _XML_ATTRIBUTE_SUCCESSFUL = "successful";
  private static final String _XML_ATTRIBUTE_MINIMUM_ROLL = "minimumRoll";
  private static final String _XML_ATTRIBUTE_RE_ROLLED = "reRolled";

  private Skill fSkill;
  private String fDefenderId;
  private int[] fRoll;
  private boolean fSuccessful;
  private int fMinimumRoll;
  private boolean fReRolled;
  
  public ReportTentaclesShadowingRoll() {
    super();
  }

  public ReportTentaclesShadowingRoll(Skill pSkill, String pDefenderId, int[] pRoll, boolean pSuccessful, int pMinimumRoll, boolean pReRolled) {
    fSkill = pSkill;
    fDefenderId = pDefenderId;
    fRoll = pRoll;
    fSuccessful = pSuccessful;
    fMinimumRoll = pMinimumRoll;
    fReRolled = pReRolled;
  }
  
  public ReportId getId() {
    return ReportId.TENTACLES_SHADOWING_ROLL;
  }

  public Skill getSkill() {
    return fSkill;
  }
  
  public String getDefenderId() {
    return fDefenderId;
  }
  
  public int[] getRoll() {
    return fRoll;
  }
  
  public boolean isSuccessful() {
    return fSuccessful;
  }
  
  public int getMinimumRoll() {
    return fMinimumRoll;
  }
  
  public boolean isReRolled() {
    return fReRolled;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportTentaclesShadowingRoll(getSkill(), getDefenderId(), getRoll(), isSuccessful(), getMinimumRoll(), isReRolled());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    String skillName = (getSkill() != null) ? getSkill().getName() : null;
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SKILL, skillName);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_DEFENDER_ID, getDefenderId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL, getRoll());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SUCCESSFUL, isSuccessful());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MINIMUM_ROLL, getMinimumRoll());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_RE_ROLLED, isReRolled());
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
    pByteList.addByte((byte) ((getSkill() != null) ? getSkill().getId() : 0));
    pByteList.addString(getDefenderId());
    pByteList.addByteArray(getRoll());
    pByteList.addBoolean(isSuccessful());
    pByteList.addByte((byte) getMinimumRoll());
    pByteList.addBoolean(isReRolled());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fSkill = new SkillFactory().forId(pByteArray.getByte());
    fDefenderId = pByteArray.getString();
    fRoll = pByteArray.getByteArrayAsIntArray();
    fSuccessful = pByteArray.getBoolean();
    fMinimumRoll = pByteArray.getByte();
    fReRolled = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
    
}
