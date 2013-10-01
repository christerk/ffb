package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.Team;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogWinningsReRollParameter;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class DialogWinningsReRollHandler extends DialogHandler {
  
  public DialogWinningsReRollHandler(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public void showDialog() {
    
    Game game = getClient().getGame();
    DialogWinningsReRollParameter dialogParameter = (DialogWinningsReRollParameter) game.getDialogParameter();

    if (dialogParameter != null) {
      
      Team team = game.getTeamHome().getId().equals(dialogParameter.getTeamId()) ? game.getTeamHome() : game.getTeamAway();
      
      if ((ClientMode.PLAYER == getClient().getMode()) && (game.getTeamHome() == team)) {
        setDialog(new DialogWinningsReRoll(getClient(), dialogParameter.getOldRoll()));
        getDialog().showDialog(this);
        
      } else {
        showStatus("Winnings", "Waiting for coach to re-roll winnings.", StatusType.WAITING);
      }
      
    }
    
  }
  
  
  public void dialogClosed(IDialog pDialog) {
    hideDialog();
    if (testDialogHasId(pDialog, DialogId.WINNINGS_RE_ROLL)) {
      DialogWinningsReRoll winningsReRollDialog = (DialogWinningsReRoll) pDialog;
      ReRollSource reRollSource = !winningsReRollDialog.isChoiceYes() ? ReRollSource.WINNINGS : null;
      getClient().getCommunication().sendUseReRoll(ReRolledAction.WINNINGS, reRollSource);
    }
  }

}
