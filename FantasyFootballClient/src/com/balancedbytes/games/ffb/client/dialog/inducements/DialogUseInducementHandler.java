package com.balancedbytes.games.ffb.client.dialog.inducements;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.dialog.DialogHandler;
import com.balancedbytes.games.ffb.client.dialog.IDialog;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogUseInducementParameter;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class DialogUseInducementHandler extends DialogHandler {
  
  public DialogUseInducementHandler(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public void showDialog() {
    
    Game game = getClient().getGame();
    DialogUseInducementParameter dialogParameter = (DialogUseInducementParameter) game.getDialogParameter();

    if (dialogParameter != null) {
      
      if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome().getId().equals(dialogParameter.getTeamId())) {
        setDialog(new DialogUseInducement(getClient(), dialogParameter));
        getDialog().showDialog(this);

      } else {
        showStatus("Use Inducement", "Waiting for coach to select an inducement.", StatusType.WAITING);
      }
      
    }

  }
  
  @Override
  public boolean isEndTurnAllowedWhileDialogVisible() {
    return false;
  }
    
  public void dialogClosed(IDialog pDialog) {
    hideDialog();
    if ((pDialog != null) && (pDialog.getId() == DialogId.USE_INDUCEMENT)) {
      DialogUseInducement useInducementDialog = (DialogUseInducement) pDialog;
      if (useInducementDialog.getInducement() != null) {
      	getClient().getCommunication().sendUseInducement(useInducementDialog.getInducement());
      } else if (useInducementDialog.getCard() != null) {
      	getClient().getCommunication().sendUseInducement(useInducementDialog.getCard());
      } else {
      	getClient().getCommunication().sendUseInducement((InducementType) null);
      }
    }
  }  

}
