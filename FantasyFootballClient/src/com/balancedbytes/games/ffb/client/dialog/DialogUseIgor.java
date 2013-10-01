package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogUseIgorParameter;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class DialogUseIgor extends DialogYesOrNoQuestion {

  private DialogUseIgorParameter fDialogParameter;
  
  public DialogUseIgor(FantasyFootballClient pClient, DialogUseIgorParameter pDialogParameter) {
    super(pClient, "Use Igor", createMessages(pClient, pDialogParameter), IIconProperty.RESOURCE_IGOR);
    fDialogParameter = pDialogParameter;
  }
  
  public DialogId getId() {
    return DialogId.USE_IGOR;
  }
  
  public String getPlayerId() {
    return fDialogParameter.getPlayerId();
  }
  
  private static String[] createMessages(FantasyFootballClient pClient, DialogUseIgorParameter pDialogParameter) {
    String[] messages = new String[2];
    messages[0] = "Do you want to use your Igor?";
    messages[1] = "Using the Igor will re-roll the failed Regeneration.";
    return messages;
  }
  
}
