package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;

/**
 * 
 * @author Kalimar
 */
public class DialogGameStatisticsHandler extends DialogHandler {
  
  // used only when showing the GameStatistics upon ending the game
  
  public DialogGameStatisticsHandler(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public void showDialog() {
    if (ClientMode.PLAYER == getClient().getMode()) {
      setDialog(new DialogGameStatistics(getClient()));
      getDialog().showDialog(this);
    }
  }
  
  public void dialogClosed(IDialog pDialog) {
    hideDialog();
  }

}
