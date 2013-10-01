package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;

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
  
}
