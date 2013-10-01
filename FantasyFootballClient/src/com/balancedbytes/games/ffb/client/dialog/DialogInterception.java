package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.dialog.DialogId;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class DialogInterception extends DialogYesOrNoQuestion {

  public DialogInterception(FantasyFootballClient pClient) {
    super(pClient, "Interception", new String[] { "Do you want to try to intercept the pass?" }, IIconProperty.GAME_REF);
  }

  public DialogId getId() {
    return DialogId.INTERCEPTION;
  }

}
