package com.balancedbytes.games.ffb.client.dialog;

import java.util.EventListener;

/**
 * 
 * @author Kalimar
 */
public interface IDialogCloseListener extends EventListener {

  public void dialogClosed(IDialog pDialog);
  
}
