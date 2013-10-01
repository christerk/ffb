package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;

/**
 * 
 * @author Kalimar
 */
public class DialogTransferPettyCashParameter extends DialogWithoutParameter {

  public DialogTransferPettyCashParameter() {
    super();
  }

  public DialogId getId() {
    return DialogId.TRANSFER_PETTY_CASH;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogTransferPettyCashParameter();
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }

}
