package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;

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
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }

}
