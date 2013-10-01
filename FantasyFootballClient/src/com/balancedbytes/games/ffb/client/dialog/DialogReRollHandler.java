package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogReRollParameter;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class DialogReRollHandler extends DialogHandler {
  
  public DialogReRollHandler(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public void showDialog() {
    
    Game game = getClient().getGame();
    DialogReRollParameter dialogReRollParameter = (DialogReRollParameter) game.getDialogParameter();

    if (dialogReRollParameter != null) {
    
      Player player = game.getPlayerById(dialogReRollParameter.getPlayerId());
      
      if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome().hasPlayer(player)) {
        setDialog(new DialogReRoll(getClient(), dialogReRollParameter));
        getDialog().showDialog(this);
        
      } else {
        StringBuilder message = new StringBuilder();     
        String reRolledActionName = (dialogReRollParameter.getReRolledAction() != null) ? dialogReRollParameter.getReRolledAction().getName() : null;
        message.append("Waiting to re-roll ").append(reRolledActionName);
        if (dialogReRollParameter.getMinimumRoll() > 0) {
          message.append(" (").append(dialogReRollParameter.getMinimumRoll()).append("+ to succeed)");
        }
        message.append(".");
        showStatus("Re-roll", message.toString(), StatusType.WAITING);
      }
            
    }
    
  }
  
  
  public void dialogClosed(IDialog pDialog) {
    hideDialog();
    if (testDialogHasId(pDialog, DialogId.RE_ROLL)) {
      DialogReRoll reRollDialog = (DialogReRoll) pDialog;
      getClient().getCommunication().sendUseReRoll(reRollDialog.getReRolledAction(), reRollDialog.getReRollSource());
    }
  }

}
