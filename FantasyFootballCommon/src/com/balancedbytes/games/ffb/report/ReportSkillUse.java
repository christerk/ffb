package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillFactory;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.SkillUseFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;




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
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fSkill = new SkillFactory().forId(pByteArray.getByte());
    fUsed = pByteArray.getBoolean();
    fSkillUse = new SkillUseFactory().forId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.SKILL.addTo(jsonObject, fSkill);
    IJsonOption.USED.addTo(jsonObject, fUsed);
    IJsonOption.SKILL_USE.addTo(jsonObject, fSkillUse);
    return jsonObject;
  }
  
  public ReportSkillUse initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fSkill = (Skill) IJsonOption.SKILL.getFrom(jsonObject);
    fUsed = IJsonOption.USED.getFrom(jsonObject);
    fSkillUse = (SkillUse) IJsonOption.SKILL_USE.getFrom(jsonObject);
    return this;
  }    

}
