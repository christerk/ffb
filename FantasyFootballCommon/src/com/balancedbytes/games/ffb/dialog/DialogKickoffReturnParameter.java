package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class DialogKickoffReturnParameter extends DialogWithoutParameter {

  public DialogKickoffReturnParameter() {
    super();
  }
  
  public DialogId getId() {
    return DialogId.KICKOFF_RETURN;
  }

  // transformation
  
  public IDialogParameter transform() {
    return new DialogKickoffReturnParameter();
  }
  
  // JSON serialization
  
  public DialogKickoffReturnParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    return this;
  }

}
