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
public class DialogSetupErrorParameter implements IDialogParameter {
  
  private String fTeamId;
  private List<String> fSetupErrors;

  public DialogSetupErrorParameter() {
    fSetupErrors = new ArrayList<String>();
  }
  
  public DialogSetupErrorParameter(String pTeamId, String[] pSetupErrors) {
    this();
    fTeamId = pTeamId;
    add(pSetupErrors);
  }
  
  public DialogId getId() {
    return DialogId.SETUP_ERROR;
  }
  
  public String getTeamId() {
    return fTeamId;
  }

  public String[] getSetupErrors() {
    return fSetupErrors.toArray(new String[fSetupErrors.size()]);
  }

  private void add(String pSetupError) {
    if (StringTool.isProvided(pSetupError)) {
      fSetupErrors.add(pSetupError);
    }
  }
  
  private void add(String[] pSetupErrors) {
    if (ArrayTool.isProvided(pSetupErrors)) {
      for (String setupError : pSetupErrors) {
        add(setupError);
      }
    }
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogSetupErrorParameter(getTeamId(), getSetupErrors());
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) getId().getId());
    pByteList.addString(getTeamId());
    pByteList.addStringArray(getSetupErrors());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    UtilDialogParameter.validateDialogId(this, new DialogIdFactory().forId(pByteArray.getByte()));
    fTeamId = pByteArray.getString();
    add(pByteArray.getStringArray());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    IJsonOption.SETUP_ERRORS.addTo(jsonObject, fSetupErrors);
    return jsonObject;
  }
  
  public DialogSetupErrorParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    add(IJsonOption.SETUP_ERRORS.getFrom(jsonObject));
    return this;
  }

}
