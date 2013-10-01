package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.PlayerChoiceMode;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogPlayerChoiceParameter;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class DialogPlayerChoiceHandler extends DialogHandler {
  
  private DialogPlayerChoiceParameter fDialogPlayerChoiceParameter;
  
  public DialogPlayerChoiceHandler(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public void showDialog() {
    
    Game game = getClient().getGame();
    fDialogPlayerChoiceParameter = (DialogPlayerChoiceParameter) game.getDialogParameter();

    if (fDialogPlayerChoiceParameter != null) {
    
      if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome().getId().equals(fDialogPlayerChoiceParameter.getTeamId())) {
        String dialogHeader = fDialogPlayerChoiceParameter.getMode().getDialogHeader(fDialogPlayerChoiceParameter.getMaxSelects());
        FieldCoordinate dialogCoordinate = null;
        String[] playerIds = fDialogPlayerChoiceParameter.getPlayerIds();
        if (fDialogPlayerChoiceParameter.getMode() != PlayerChoiceMode.CARD) {
          int maxX = 0, maxY = 0;
          for (int i = 0; i < playerIds.length; i++) {
            Player player = game.getPlayerById(playerIds[i]);
            FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
            if (playerCoordinate.getX() > maxX) {
              maxX = playerCoordinate.getX();
            }
            if (playerCoordinate.getY() > maxY) {
              maxY = playerCoordinate.getY();
            }
          }
          dialogCoordinate = new FieldCoordinate(maxX, maxY);
        }
        setDialog(new DialogPlayerChoice(getClient(), dialogHeader, playerIds, fDialogPlayerChoiceParameter.getDescriptions(), fDialogPlayerChoiceParameter.getMaxSelects(), dialogCoordinate));
        getDialog().showDialog(this);
        
      } else {
        showStatus(fDialogPlayerChoiceParameter.getMode().getStatusTitle(), fDialogPlayerChoiceParameter.getMode().getStatusMessage(), StatusType.WAITING);
      }
      
    }
    
  }
  
  public void dialogClosed(IDialog pDialog) {
    hideDialog();
    if (testDialogHasId(pDialog, DialogId.PLAYER_CHOICE)) {
      DialogPlayerChoice playerChoiceDialog = (DialogPlayerChoice) pDialog;
      getClient().getCommunication().sendPlayerChoice(fDialogPlayerChoiceParameter.getMode(), playerChoiceDialog.getSelectedPlayers());
    }
  }

}
