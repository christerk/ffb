package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class DialogDefenderActionParameter extends DialogWithoutParameter {

  public DialogDefenderActionParameter() {
    super();
  }

  public DialogId getId() {
    return DialogId.DEFENDER_ACTION;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogDefenderActionParameter();
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  // JSON serialization
  
  public DialogDefenderActionParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    return this;
  }
  
}
