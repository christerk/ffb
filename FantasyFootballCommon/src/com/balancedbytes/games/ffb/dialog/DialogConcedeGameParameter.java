package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;

/**
 * 
 * @author Kalimar
 */
public class DialogConcedeGameParameter extends DialogWithoutParameter {

  public DialogConcedeGameParameter() {
    super();
  }

  public DialogId getId() {
    return DialogId.CONCEDE_GAME;
  }

  // transformation
  
  public IDialogParameter transform() {
    return new DialogConcedeGameParameter();
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
}

