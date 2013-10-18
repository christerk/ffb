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
public class DialogParameterFactory {

  // JSON serialization
  
  public IDialogParameter forJsonValue(JsonValue pJsonValue) {
    if ((pJsonValue == null) || pJsonValue.isNull()) {
      return null;
    }
    IDialogParameter dialogParameter = null;
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    DialogId dialogId = (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject);
    if (dialogId != null) {
      dialogParameter = dialogId.createDialogParameter();
      if (dialogParameter != null) {
        dialogParameter.initFrom(pJsonValue);
      }
    }
    return dialogParameter;
  }

}
