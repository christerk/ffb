package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class DialogBlockRollParameter implements IDialogParameter {

  private String fChoosingTeamId;
  private int fNrOfDice;
  private int[] fBlockRoll;
  private boolean fTeamReRollOption;
  private boolean fProReRollOption;

  public DialogBlockRollParameter() {
    super();
  }
  
  public DialogBlockRollParameter(String pChoosingTeamId, int pNrOfDice, int[] pBlockRoll, boolean pTeamReRollOption, boolean pProReRollOption) {
    fChoosingTeamId = pChoosingTeamId;
    fNrOfDice = pNrOfDice;
    fBlockRoll = pBlockRoll;
    fTeamReRollOption = pTeamReRollOption;
    fProReRollOption = pProReRollOption;
  }
  
  public DialogId getId() {
    return DialogId.BLOCK_ROLL;
  }

  public String getChoosingTeamId() {
    return fChoosingTeamId;
  }
  
  public int getNrOfDice() {
    return fNrOfDice;
  }
  
  public int[] getBlockRoll() {
    return fBlockRoll;
  }
  
  public boolean hasTeamReRollOption() {
    return fTeamReRollOption;
  }
  
  public boolean hasProReRollOption() {
    return fProReRollOption;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogBlockRollParameter(getChoosingTeamId(), getNrOfDice(), getBlockRoll(), hasTeamReRollOption(), hasProReRollOption());
  }

  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    UtilDialogParameter.validateDialogId(this, new DialogIdFactory().forId(pByteArray.getByte()));
    fChoosingTeamId = pByteArray.getString();
    fNrOfDice = pByteArray.getByte();
    fBlockRoll = pByteArray.getByteArrayAsIntArray();
    fTeamReRollOption = pByteArray.getBoolean();
    fProReRollOption = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    IJsonOption.CHOOSING_TEAM_ID.addTo(jsonObject, fChoosingTeamId);
    IJsonOption.NR_OF_DICE.addTo(jsonObject, fNrOfDice);
    IJsonOption.BLOCK_ROLL.addTo(jsonObject, fBlockRoll);
    IJsonOption.TEAM_RE_ROLL_OPTION.addTo(jsonObject, fTeamReRollOption);
    IJsonOption.PRO_RE_ROLL_OPTION.addTo(jsonObject, fProReRollOption);
    return jsonObject;
  }
  
  public DialogBlockRollParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    fChoosingTeamId = IJsonOption.CHOOSING_TEAM_ID.getFrom(jsonObject);
    fNrOfDice = IJsonOption.NR_OF_DICE.getFrom(jsonObject);
    fBlockRoll = IJsonOption.BLOCK_ROLL.getFrom(jsonObject);
    fTeamReRollOption = IJsonOption.TEAM_RE_ROLL_OPTION.getFrom(jsonObject);
    fProReRollOption = IJsonOption.PRO_RE_ROLL_OPTION.getFrom(jsonObject);
    return this;
  }

}
