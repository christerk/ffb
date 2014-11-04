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
public class DialogJoinParameter extends DialogWithoutParameter {

  public DialogJoinParameter() {
    super();
  }

  public DialogId getId() {
    return DialogId.JOIN;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogJoinParameter();
  }
  
  // JSON serialization
  
  public DialogJoinParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    return this;
  }

}
