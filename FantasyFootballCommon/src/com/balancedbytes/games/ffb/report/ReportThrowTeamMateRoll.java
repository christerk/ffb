package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.PassingDistanceFactory;
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
public class ReportThrowTeamMateRoll extends ReportSkillRoll {
  
  private String fThrownPlayerId;
  private PassingDistance fPassingDistance;
  
  public ReportThrowTeamMateRoll() {    
    super(ReportId.THROW_TEAM_MATE_ROLL);
  }

  public ReportThrowTeamMateRoll(String pThrowerId, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled, PassModifier[] pPassModifiers, PassingDistance pPassingDistance, String pThrownPlayerId) {
    super(ReportId.THROW_TEAM_MATE_ROLL, pThrowerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pPassModifiers);
    fThrownPlayerId = pThrownPlayerId;
    fPassingDistance = pPassingDistance;
  }

  public String getThrownPlayerId() {
    return fThrownPlayerId;
  }
  
  public PassingDistance getPassingDistance() {
    return fPassingDistance;
  }
  
  public PassModifier[] getPassModifiers() {
    return (PassModifier[]) getRollModifiers();
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportThrowTeamMateRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(), getPassModifiers(), getPassingDistance(), getThrownPlayerId());
  }
    
  // ByteArray serialization
  
  @Override
  public int getByteArraySerializationVersion() {
    return 1;
  }

  @Override
  public void addTo(ByteList pByteList) {
    super.addTo(pByteList);
    pByteList.addString(getThrownPlayerId());
    pByteList.addByte((byte) ((getPassingDistance() != null) ? getPassingDistance().getId() : 0));
  }

  @Override
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = super.initFrom(pByteArray);
    fThrownPlayerId = pByteArray.getString();
    fPassingDistance = new PassingDistanceFactory().forId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }

  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = UtilJson.toJsonObject(super.toJsonValue());
    IJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, fThrownPlayerId);
    IJsonOption.PASSING_DISTANCE.addTo(jsonObject, fPassingDistance);
    return jsonObject;
  }
  
  @Override
  public ReportThrowTeamMateRoll initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fThrownPlayerId = IJsonOption.THROWN_PLAYER_ID.getFrom(jsonObject);
    fPassingDistance = (PassingDistance) IJsonOption.PASSING_DISTANCE.getFrom(jsonObject);
    return this;
  }

}
