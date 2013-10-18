package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public interface IDialogParameter extends IByteArraySerializable, IJsonSerializable {
  
  public DialogId getId();
  
  public IDialogParameter transform();
  
  // overrides IJsonSerializable
  public IDialogParameter initFrom(JsonValue pJsonValue);

  // overrides IJsonSerializable
  public JsonObject toJsonValue();
  
}
