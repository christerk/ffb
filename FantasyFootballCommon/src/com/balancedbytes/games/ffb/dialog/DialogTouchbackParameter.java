package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;

/**
 * 
 * @author Kalimar
 */
public class DialogTouchbackParameter extends DialogWithoutParameter {

  public DialogTouchbackParameter() {
    super();
  }

  public DialogId getId() {
    return DialogId.TOUCHBACK;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogTouchbackParameter();
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }

}
