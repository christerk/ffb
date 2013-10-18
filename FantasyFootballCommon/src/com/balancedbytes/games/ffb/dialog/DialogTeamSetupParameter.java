package com.balancedbytes.games.ffb.dialog;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class DialogTeamSetupParameter implements IDialogParameter {
  
  private boolean fLoadDialog;
  private List<String> fSetupNames;

  public DialogTeamSetupParameter() {
    fSetupNames = new ArrayList<String>();
  }
  
  public DialogTeamSetupParameter(boolean pLoadDialog, String[] pSetupNames) {
    this();
    fLoadDialog = pLoadDialog;
    add(pSetupNames);
  }
  
  public DialogId getId() {
    return DialogId.TEAM_SETUP;
  }
  
  public boolean isLoadDialog() {
    return fLoadDialog;
  }

  public String[] getSetupNames() {
    return fSetupNames.toArray(new String[fSetupNames.size()]);
  }

  private void add(String pSetupName) {
    if (StringTool.isProvided(pSetupName)) {
      fSetupNames.add(pSetupName);
    }
  }
  
  private void add(String[] pSetupNames) {
    if (ArrayTool.isProvided(pSetupNames)) {
      for (String setupError : pSetupNames) {
        add(setupError);
      }
    }
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogTeamSetupParameter(isLoadDialog(), getSetupNames());
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) getId().getId());
    pByteList.addBoolean(isLoadDialog());
    pByteList.addStringArray(getSetupNames());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    UtilDialogParameter.validateDialogId(this, new DialogIdFactory().forId(pByteArray.getByte()));
    fLoadDialog = pByteArray.getBoolean();
    add(pByteArray.getStringArray());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    IJsonOption.LOAD_DIALOG.addTo(jsonObject, fLoadDialog);
    IJsonOption.SETUP_NAMES.addTo(jsonObject, fSetupNames);
    return jsonObject;
  }
  
  public DialogTeamSetupParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    fLoadDialog = IJsonOption.LOAD_DIALOG.getFrom(jsonObject);
    add(IJsonOption.SETUP_NAMES.getFrom(jsonObject));
    return this;
  }

}
