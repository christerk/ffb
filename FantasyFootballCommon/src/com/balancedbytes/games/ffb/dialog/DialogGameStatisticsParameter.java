package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;

/**
 * 
 * @author Kalimar
 */
public class DialogGameStatisticsParameter extends DialogWithoutParameter {

  public DialogGameStatisticsParameter() {
    super();
  }

  public DialogId getId() {
    return DialogId.GAME_STATISTICS;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogGameStatisticsParameter();
  }

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }

}
