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
public class DialogFollowupChoiceParameter extends DialogWithoutParameter {

  public DialogFollowupChoiceParameter() {
    super();
  }

  public DialogId getId() {
    return DialogId.FOLLOWUP_CHOICE;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogFollowupChoiceParameter();
  }
  
  // JSON serialization
  
  public DialogFollowupChoiceParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    return this;
  }

}
