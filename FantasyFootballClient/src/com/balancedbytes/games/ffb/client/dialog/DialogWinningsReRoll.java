package com.balancedbytes.games.ffb.client.dialog;

import java.awt.event.ActionListener;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.dialog.DialogId;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class DialogWinningsReRoll extends DialogYesOrNoQuestion implements ActionListener {
  
  public DialogWinningsReRoll(FantasyFootballClient pClient, int pOldRoll) {
    super(pClient, "Re-roll Winnings", createMessages(pOldRoll), IIconProperty.GAME_DICE_SMALL, "Keep", (int) 'K', "Re-Roll", (int) 'R');
  }
  
  public DialogId getId() {
    return DialogId.WINNINGS_RE_ROLL;
  }
  
  private static String[] createMessages(int pOldRoll) {
    String[] messages = new String[3];
    messages[0] = "Do you want to keep your winnings?";
    messages[1] = "The current roll is " + pOldRoll + ".";
    if (pOldRoll < 6) {
      messages[2] = "If you re-roll you must keep the new result.";
    } else {
      messages[2] = "Rolled maximum. If you re-roll this it can only get worse.";
    }
    return messages;
  }
  
}
