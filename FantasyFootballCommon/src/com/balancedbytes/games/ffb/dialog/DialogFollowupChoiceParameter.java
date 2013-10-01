package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;

/**
 * 
 * @author Kalimar
 */
public class DialogFollowupChoiceParameter extends DialogWithoutParameter {

  public DialogFollowupChoiceParameter() {
    super();
  }

  public DialogId getId() {
    return DialogId.FOLLOWUP_CHOICE;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogFollowupChoiceParameter();
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }

}
