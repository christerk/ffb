package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ReportConfusionRoll implements IReport {
  
  private String fPlayerId;
  private Skill fConfusionSkill;
  private boolean fSuccessful;
  private int fRoll;
  private int fMinimumRoll;
  private boolean fReRolled;

  public ReportConfusionRoll() {
    super();
  }
  
  public ReportConfusionRoll(String pPlayerId, Skill pConfusionSkill, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled) {
    fPlayerId = pPlayerId;
    fConfusionSkill = pConfusionSkill;
    fSuccessful = pSuccessful;
    fRoll = pRoll;
    fMinimumRoll = pMinimumRoll;
    fReRolled = pReRolled;
  }

  public ReportId getId() {
    return ReportId.CONFUSION_ROLL;
  }

  public String getPlayerId() {
    return fPlayerId;
  }

  public Skill getConfusionSkill() {
    return fConfusionSkill;
  }
  
  public boolean isSuccessful() {
    return fSuccessful;
  }

  public int getRoll() {
    return fRoll;
  }

  public int getMinimumRoll() {
    return fMinimumRoll;
  }

  public boolean isReRolled() {
    return fReRolled;
  }

  // transformation
  
  public IReport transform() {
    return new ReportConfusionRoll(getPlayerId(), getConfusionSkill(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled());
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getPlayerId());
    pByteList.addBoolean(isSuccessful());
    pByteList.addByte((byte) getRoll());
    pByteList.addByte((byte) getMinimumRoll());
    pByteList.addByte((byte) 0);  // nr of modifiers
    pByteList.addBoolean(isReRolled());
    pByteList.addByte((byte) ((getConfusionSkill() != null) ? getConfusionSkill().getId() : 0));
  }

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fSuccessful = pByteArray.getBoolean();
    fRoll = pByteArray.getByte();
    fMinimumRoll = pByteArray.getByte();
    pByteArray.getByte();  // nr of modifiers
    fReRolled = pByteArray.getBoolean();
    fConfusionSkill = new SkillFactory().forId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
    IJsonOption.ROLL.addTo(jsonObject, fRoll);
    IJsonOption.MINIMUM_ROLL.addTo(jsonObject, fMinimumRoll);
    IJsonOption.RE_ROLLED.addTo(jsonObject, fReRolled);
    IJsonOption.CONFUSION_SKILL.addTo(jsonObject, fConfusionSkill);
    return jsonObject;
  }
  
  public ReportConfusionRoll initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fSuccessful = IJsonOption.SUCCESSFUL.getFrom(jsonObject);
    fRoll = IJsonOption.ROLL.getFrom(jsonObject);
    fMinimumRoll = IJsonOption.MINIMUM_ROLL.getFrom(jsonObject);
    fReRolled = IJsonOption.RE_ROLLED.getFrom(jsonObject);
    fConfusionSkill = (Skill) IJsonOption.CONFUSION_SKILL.getFrom(jsonObject);
    return this;
  }
    
}
