package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;

public class DialogKickoffReturnParameter extends DialogWithoutParameter {

  public DialogKickoffReturnParameter() {
    super();
  }
  
  public DialogId getId() {
    return DialogId.KICKOFF_RETURN;
  }

  // transformation
  
  public IDialogParameter transform() {
    return new DialogKickoffReturnParameter();
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }

}
