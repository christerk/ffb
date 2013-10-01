package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogJourneymenParameter;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class DialogJourneymenHandler extends DialogHandler {
  
  public DialogJourneymenHandler(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public void showDialog() {
    
    Game game = getClient().getGame();
    DialogJourneymenParameter dialogJourneymenParameter = (DialogJourneymenParameter) game.getDialogParameter();

    if (dialogJourneymenParameter != null) {
    
      if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome().getId().equals(dialogJourneymenParameter.getTeamId())) {
        setDialog(new DialogJourneymen(getClient(), dialogJourneymenParameter.getSlots(), dialogJourneymenParameter.getPositionIds()));
        getDialog().showDialog(this);
        
      } else {
        showStatus("Journeymen", "Waiting for coach to hire up to " + dialogJourneymenParameter.getSlots() + " Journeymen.", StatusType.WAITING);
      }
      
    }
    
  }
  
  public void dialogClosed(IDialog pDialog) {
    hideDialog();
    if (testDialogHasId(pDialog, DialogId.JOURNEYMEN)) {
      DialogJourneymen journeymenDialog = (DialogJourneymen) pDialog;
      getClient().getCommunication().sendJourneymen(journeymenDialog.getPositionIds(), journeymenDialog.getSlotsSelected());
    }
  }

}
