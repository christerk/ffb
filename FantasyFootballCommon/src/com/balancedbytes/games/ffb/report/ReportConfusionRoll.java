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
public class ReportConfusionRoll extends ReportSkillRoll {
  
  private Skill fConfusionSkill;

  public ReportConfusionRoll() {
    super(ReportId.CONFUSION_ROLL);
  }
  
  public ReportConfusionRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled, Skill pConfusionSkill) {
    super(ReportId.CONFUSION_ROLL, pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled);
    fConfusionSkill = pConfusionSkill;
  }

  public ReportId getId() {
    return ReportId.CONFUSION_ROLL;
  }

  public Skill getConfusionSkill() {
    return fConfusionSkill;
  }

  // transformation
  
  public IReport transform() {
    return new ReportConfusionRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(), getConfusionSkill());
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }

  public void addTo(ByteList pByteList) {
    super.addTo(pByteList);
    pByteList.addByte((byte) ((getConfusionSkill() != null) ? getConfusionSkill().getId() : 0));
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = super.initFrom(pByteArray);
    fConfusionSkill = new SkillFactory().forId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonValue toJsonValue() {
    JsonObject jsonObject = UtilJson.toJsonObject(super.toJsonValue());
    IJsonOption.CONFUSION_SKILL.addTo(jsonObject, fConfusionSkill);
    return jsonObject;
  }
  
  @Override
  public ReportConfusionRoll initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fConfusionSkill = (Skill) IJsonOption.CONFUSION_SKILL.getFrom(jsonObject);
    return this;
  }
    
}
