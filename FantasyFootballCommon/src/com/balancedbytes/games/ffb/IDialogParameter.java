package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.json.IJsonSerializable;

/**
 * 
 * @author Kalimar
 */
public interface IDialogParameter extends IByteArraySerializable, IJsonSerializable {
  
  public DialogId getId();
  
  public IDialogParameter transform();
  
}
