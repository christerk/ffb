package com.balancedbytes.games.ffb.report;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.CatchModifierFactory;
import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.IRollModifierFactory;
import com.balancedbytes.games.ffb.InterceptionModifierFactory;
import com.balancedbytes.games.ffb.PassModifierFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
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
  private List<IRollModifier> fRollModifierList;
  
  public ReportSkillRoll(ReportId pId) {
    fId = pId;
    initRollModifiers(null);
  }

  public ReportSkillRoll(ReportId pId, String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled) {
    this(pId, pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, null);
  }

  public ReportSkillRoll(ReportId pId, String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled, IRollModifier[] pRollModifiers) {
    fId = pId;
    fPlayerId = pPlayerId;
    fSuccessful = pSuccessful;
    fRoll = pRoll;
    fMinimumRoll = pMinimumRoll;
    fReRolled = pReRolled;
    initRollModifiers(pRollModifiers);
  }
  
  private void initRollModifiers(IRollModifier[] pRollModifiers) {
    fRollModifierList = new ArrayList<IRollModifier>();
    if (ArrayTool.isProvided(pRollModifiers)) {
      for (IRollModifier rollModifier : pRollModifiers) {
        addRollModifier(rollModifier);
      }
    }
  }
  
  public void addRollModifier(IRollModifier pRollModifier) {
    if (pRollModifier != null) {
      fRollModifierList.add(pRollModifier);
    }
  }
  
  public IRollModifier[] getRollModifiers() {
    return fRollModifierList.toArray(new IRollModifier[fRollModifierList.size()]);
  }
  
  public boolean hasRollModifier(IRollModifier pRollModifier) {
    return fRollModifierList.contains(pRollModifier);
  }

  protected List<IRollModifier> getRollModifierList() {
    return fRollModifierList;
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
  
  // transformation
  
  public IReport transform() {
    return new ReportSkillRoll(getId(), getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(), getRollModifiers());
  }
  
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fSuccessful = pByteArray.getBoolean();
    fRoll = pByteArray.getByte();
    fMinimumRoll = pByteArray.getByte();
    fRollModifierList.clear();
    int nrOfModifiers = pByteArray.getByte();
    if (nrOfModifiers > 0) {
      IRollModifierFactory modifierFactory = createRollModifierFactory();
      for (int i = 0; i < nrOfModifiers; i++) {
        int modifierId = pByteArray.getByte();
        if (modifierFactory != null) {
          fRollModifierList.add(modifierFactory.forId(modifierId));
        }
      }
    }
    fReRolled = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, fId);
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
    IJsonOption.ROLL.addTo(jsonObject, fRoll);
    IJsonOption.MINIMUM_ROLL.addTo(jsonObject, fMinimumRoll);
    IJsonOption.RE_ROLLED.addTo(jsonObject, fReRolled);
    if (fRollModifierList.size() > 0) {
      JsonArray modifierArray = new JsonArray();
      for (IRollModifier modifier : fRollModifierList) {
        modifierArray.add(UtilJson.toJsonValue(modifier));
      }
      IJsonOption.ROLL_MODIFIERS.addTo(jsonObject, modifierArray);
    }
    return jsonObject;
  }
  
  public ReportSkillRoll initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fSuccessful = IJsonOption.SUCCESSFUL.getFrom(jsonObject);
    fRoll = IJsonOption.ROLL.getFrom(jsonObject);
    fMinimumRoll = IJsonOption.MINIMUM_ROLL.getFrom(jsonObject);
    fReRolled = IJsonOption.RE_ROLLED.getFrom(jsonObject);
    JsonArray modifierArray = IJsonOption.ROLL_MODIFIERS.getFrom(jsonObject);
    if (modifierArray != null) {
      IRollModifierFactory modifierFactory = createRollModifierFactory();
      if (modifierFactory != null) {
        for (int i = 0; i < modifierArray.size(); i++) {
          fRollModifierList.add((IRollModifier) UtilJson.toEnumWithName(modifierFactory, modifierArray.get(i)));
        }
      }
    }
    return this;
  }
  
  private IRollModifierFactory createRollModifierFactory() {
    switch (getId()) {
      case CATCH_ROLL:
        return new CatchModifierFactory();
      case DODGE_ROLL:
        // TODO: return DodgeModifierFactory
        return null;
      case GO_FOR_IT_ROLL:
        // TODO: return GoForItModifierFactory
        return null;
      case INTERCEPTION_ROLL:
        return new InterceptionModifierFactory();
      case LEAP_ROLL:
        // TODO: return LeapModifierFactory
        return null;
      case PASS_ROLL:
      case THROW_TEAM_MATE_ROLL:
        return new PassModifierFactory();
      case PICK_UP_ROLL:
        // TODO: return PickUpModifierFactory
        return null;
      case RIGHT_STUFF_ROLL:
        // TODO: return RightStuffModifierFactory
        return null;
      case HYPNOTIC_GAZE_ROLL:
        // TODO: return GazeModifierFactory
        return null;
      default:
        return null;
    }
  }
  
}
