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
public class DialogTouchbackParameter extends DialogWithoutParameter {

  public DialogTouchbackParameter() {
    super();
  }

  public DialogId getId() {
    return DialogId.TOUCHBACK;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogTouchbackParameter();
  }
  
  // JSON serialization
  
  public DialogTouchbackParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    return this;
  }

}
