package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillFactory;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;




/**
 * 
 * @author Kalimar
 */
public class ReportSkillUse implements IReport {
  
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  private static final String _XML_ATTRIBUTE_SKILL = "skill";
  private static final String _XML_ATTRIBUTE_USED = "used";
  private static final String _XML_ATTRIBUTE_SKILL_USE = "skillUse";

  private String fPlayerId;
  private Skill fSkill;
  private boolean fUsed;
  private SkillUse fSkillUse;
  
  public ReportSkillUse() {
    super();
  }

  public ReportSkillUse(Skill pSkill, boolean pUsed, SkillUse pSkillUse) {
  	this(null, pSkill, pUsed, pSkillUse);
  }

  public ReportSkillUse(String pPlayerId, Skill pSkill, boolean pUsed, SkillUse pSkillUse) {
    fPlayerId = pPlayerId;
    fSkill = pSkill;
    fUsed = pUsed;
    fSkillUse = pSkillUse;
  }

  public ReportId getId() {
    return ReportId.SKILL_USE;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }
  
  public Skill getSkill() {
    return fSkill;
  }
  
  public boolean isUsed() {
    return fUsed;
  }
  
  public SkillUse getSkillUse() {
    return fSkillUse;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportSkillUse(getPlayerId(), getSkill(), isUsed(), getSkillUse());
  }
    
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
    String skillName = (getSkill() != null) ? getSkill().getName() : null;
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SKILL, skillName);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_USED, isUsed());
    String skillUseName = (getSkillUse() != null) ? getSkillUse().getName() : null;
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SKILL_USE, skillUseName);
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
    pByteList.addByte((byte) ((getSkill() != null) ? getSkill().getId() : 0));
    pByteList.addBoolean(isUsed());
    pByteList.addByte((byte) ((getSkillUse() != null) ? getSkillUse().getId() : 0));
  }

  public int initFrom(ByteArray pByteArray) {
    ReportId reportId = ReportId.fromId(pByteArray.getSmallInt());
    if (getId() != reportId) {
      throw new IllegalStateException("Wrong report id. Expected " + getId().getName() + " received " + ((reportId != null) ? reportId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fSkill = new SkillFactory().forId(pByteArray.getByte());
    fUsed = pByteArray.getBoolean();
    fSkillUse = SkillUse.fromId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }
      
}
