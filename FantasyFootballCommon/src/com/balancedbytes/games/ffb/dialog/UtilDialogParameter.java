package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;

/**
 * 
 * @author Kalimar
 */
public class UtilDialogParameter {
  
  public static void validateDialogId(IDialogParameter pDialogParameter, DialogId pReceivedId) {
    if (pDialogParameter == null) {
      throw new IllegalArgumentException("Parameter dialogParameter must not be null.");
    }
    if (pDialogParameter.getId() != pReceivedId) {
      throw new IllegalStateException("Wrong dialog id. Expected " + pDialogParameter.getId().getName() + " received " + ((pReceivedId != null) ? pReceivedId.getName() : "null"));
    }
  }

}
