package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogReceiveChoiceParameter;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class DialogReceiveChoiceHandler extends DialogHandler {
  
  public DialogReceiveChoiceHandler(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public void showDialog() {
    
    Game game = getClient().getGame();
    DialogReceiveChoiceParameter dialogReceiveChoiceParameter = (DialogReceiveChoiceParameter) game.getDialogParameter();

    if (dialogReceiveChoiceParameter != null) {
    
      if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome().getId().equals(dialogReceiveChoiceParameter.getChoosingTeamId())) {
        setDialog(new DialogReceiveChoice(getClient()));
        getDialog().showDialog(this);
        
      } else {
        showStatus("Receive Choice", "Waiting for coach to choose to kick or receive.", StatusType.WAITING);
      }
      
    }
    
  }
  
  
  public void dialogClosed(IDialog pDialog) {
    hideDialog();
    if (testDialogHasId(pDialog, DialogId.RECEIVE_CHOICE)) {
      DialogReceiveChoice receiveDialog = (DialogReceiveChoice) pDialog;
      getClient().getCommunication().sendReceiveChoice(receiveDialog.isChoiceYes());
    }
  }

}
