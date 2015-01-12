package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.eclipsesource.json.JsonObject;

/**
 * 
 * @author Kalimar
 */
public abstract class DialogWithoutParameter implements IDialogParameter {

  public DialogWithoutParameter() {
    super();
  }

  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    return jsonObject;
  }

}
