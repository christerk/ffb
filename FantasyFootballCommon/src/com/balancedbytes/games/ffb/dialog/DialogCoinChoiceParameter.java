package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;

/**
 * 
 * @author Kalimar
 */
public class DialogCoinChoiceParameter extends DialogWithoutParameter {

  public DialogCoinChoiceParameter() {
    super();
  }

  public DialogId getId() {
    return DialogId.COIN_CHOICE;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogCoinChoiceParameter();
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }

}
