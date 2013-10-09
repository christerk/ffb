package com.balancedbytes.games.ffb.dialog;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillFactory;
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
public class DialogSkillUseParameter implements IDialogParameter {
  
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  private static final String _XML_ATTRIBUTE_SKILL = "skill";
  private static final String _XML_ATTRIBUTE_MINIMUM_ROLL = "minimumRoll";
  
  private String fPlayerId;
  private Skill fSkill;
  private int fMinimumRoll;

  public DialogSkillUseParameter() {
    super();
  }
  
  public DialogSkillUseParameter(String pPlayerId, Skill pSkill, int pMinimumRoll) {
    fPlayerId = pPlayerId;
    fSkill = pSkill;
    fMinimumRoll = pMinimumRoll;
  }
  
  public DialogId getId() {
    return DialogId.SKILL_USE;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }
  
  public Skill getSkill() {
    return fSkill;
  }
  
  public int getMinimumRoll() {
    return fMinimumRoll;
  }

  // transformation
  
  public IDialogParameter transform() {
    return new DialogSkillUseParameter(getPlayerId(), getSkill(), getMinimumRoll());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SKILL, (getSkill() != null) ? getSkill().getName() : null);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MINIMUM_ROLL, getMinimumRoll());
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
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) getId().getId());
    pByteList.addString(getPlayerId());
    pByteList.addByte((byte) ((getSkill() != null) ? getSkill().getId() : 0));
    pByteList.addByte((byte) getMinimumRoll());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    UtilDialogParameter.validateDialogId(this, new DialogIdFactory().forId(pByteArray.getByte()));
    fPlayerId = pByteArray.getString();
    fSkill = new SkillFactory().forId(pByteArray.getByte());
    fMinimumRoll = pByteArray.getByte();
    return byteArraySerializationVersion;
  }

  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.SKILL.addTo(jsonObject, fSkill);
    IJsonOption.MINIMUM_ROLL.addTo(jsonObject, fMinimumRoll);
    return jsonObject;
  }
  
  public DialogSkillUseParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fSkill = (Skill) IJsonOption.SKILL.getFrom(jsonObject);
    fMinimumRoll = IJsonOption.MINIMUM_ROLL.getFrom(jsonObject);
    return this;
  }
  
}
