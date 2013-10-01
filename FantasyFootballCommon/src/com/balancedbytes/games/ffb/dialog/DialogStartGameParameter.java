package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;

/**
 * 
 * @author Kalimar
 */
public class DialogStartGameParameter extends DialogWithoutParameter {

  public DialogStartGameParameter() {
    super();
  }

  public DialogId getId() {
    return DialogId.START_GAME;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogStartGameParameter();
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
}
