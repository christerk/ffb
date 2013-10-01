package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;

/**
 * 
 * @author Kalimar
 */
public class DialogPassBlockParameter extends DialogWithoutParameter {

  public DialogPassBlockParameter() {
    super();
  }

  public DialogId getId() {
    return DialogId.PASS_BLOCK;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogPassBlockParameter();
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }

}
