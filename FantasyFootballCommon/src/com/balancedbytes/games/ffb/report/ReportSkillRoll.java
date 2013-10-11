package com.balancedbytes.games.ffb.report;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.IRollModifierFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ReportSkillRoll implements IReport {
  
  private ReportId fId;
  private String fPlayerId;
  private boolean fSuccessful;
  private int fRoll;
  private int fMinimumRoll;
  private boolean fReRolled;
  private List<IRollModifier> fRollModifiers;
  
  public ReportSkillRoll(ReportId pId) {
    fId = pId;
    fRollModifiers = new ArrayList<IRollModifier>();
  }

  public ReportSkillRoll(ReportId pId, String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled) {
    this(pId);
    fPlayerId = pPlayerId;
    fSuccessful = pSuccessful;
    fRoll = pRoll;
    fMinimumRoll = pMinimumRoll;
    fReRolled = pReRolled;
  }
  
  public ReportId getId() {
    return fId;
  }

  public String getPlayerId() {
    return fPlayerId;
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
  
  protected List<IRollModifier> getRollModifiers() {
    return fRollModifiers;
  }
  
  protected void addRollModifier(IRollModifier pRollModifier) {
    if (pRollModifier != null) {
      getRollModifiers().add(pRollModifier);
    }
  }

  protected void addRollModifiers(IRollModifier[] pRollModifiers) {
    if (ArrayTool.isProvided(pRollModifiers)) {
      for (IRollModifier rollModifier : pRollModifiers) {
        addRollModifier(rollModifier);
      }
    }
  }
  
  // needs to be overwritten by subclasses to instantiate RollModifiers
  protected IRollModifierFactory createRollModifierFactory() {
    return null;
  }
    
  // transformation
  
  public IReport transform() {
    return new ReportSkillRoll(getId(), getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled());
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(fId.getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(fPlayerId);
    pByteList.addBoolean(fSuccessful);
    pByteList.addByte((byte) fRoll);
    pByteList.addByte((byte) fMinimumRoll);
    pByteList.addByte((byte) fRollModifiers.size());
    for (IRollModifier rollModifier : fRollModifiers) {
      pByteList.addByte((byte) rollModifier.getId()); 
    }
    pByteList.addBoolean(isReRolled());
  }

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fSuccessful = pByteArray.getBoolean();
    fRoll = pByteArray.getByte();
    fMinimumRoll = pByteArray.getByte();
    int nrOfModifiers = pByteArray.getByte();
    IRollModifierFactory rollModifierFactory = createRollModifierFactory();
    for (int i = 0; i < nrOfModifiers; i++) {
      int rollModifierId = pByteArray.getByte();
      if (rollModifierFactory != null) {
        fRollModifiers.add(rollModifierFactory.forId(rollModifierId));
      }
    }
    fReRolled = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, fId);
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
    IJsonOption.ROLL.addTo(jsonObject, fRoll);
    IJsonOption.MINIMUM_ROLL.addTo(jsonObject, fMinimumRoll);
    if (fRollModifiers.size() > 0) {
      JsonArray rollModifierArray = new JsonArray();
      for (IRollModifier modifier : fRollModifiers) {
        rollModifierArray.add(UtilJson.toJsonValue(modifier));
      }
      IJsonOption.ROLL_MODIFIERS.addTo(jsonObject, rollModifierArray);
    }
    IJsonOption.RE_ROLLED.addTo(jsonObject, fReRolled);
    return jsonObject;
  }
  
  public ReportSkillRoll initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fSuccessful = IJsonOption.SUCCESSFUL.getFrom(jsonObject);
    fRoll = IJsonOption.ROLL.getFrom(jsonObject);
    fMinimumRoll = IJsonOption.MINIMUM_ROLL.getFrom(jsonObject);
    fRollModifiers.clear();
    JsonArray rollModifierArray = IJsonOption.ROLL_MODIFIERS.getFrom(jsonObject);
    IRollModifierFactory rollModifierFactory = createRollModifierFactory();
    if ((rollModifierArray != null) && (rollModifierFactory != null)) {
      for (int i = 0; i < rollModifierArray.size(); i++) {
        fRollModifiers.add((IRollModifier) UtilJson.toEnumWithName(rollModifierFactory, rollModifierArray.get(i)));
      }
    }
    fReRolled = IJsonOption.RE_ROLLED.getFrom(jsonObject);
    return this;
  }
  
}
