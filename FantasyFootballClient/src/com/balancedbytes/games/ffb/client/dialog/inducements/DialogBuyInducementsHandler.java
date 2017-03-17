package com.balancedbytes.games.ffb.client.dialog.inducements;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.dialog.DialogHandler;
import com.balancedbytes.games.ffb.client.dialog.IDialog;
import com.balancedbytes.games.ffb.dialog.DialogBuyInducementsParameter;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.model.Game;

public class DialogBuyInducementsHandler extends DialogHandler {

  public DialogBuyInducementsHandler(FantasyFootballClient pClient) {
    super(pClient);
  }

  public void showDialog() {

    Game game = getClient().getGame();
    DialogBuyInducementsParameter dialogParameter = (DialogBuyInducementsParameter) game.getDialogParameter();

    if (dialogParameter != null) {
      if ((ClientMode.PLAYER == getClient().getMode()) && (game.getTeamHome().getId().equals(dialogParameter.getTeamId()))) {
        setDialog(new DialogBuyInducements(
          getClient(),
          dialogParameter.getTeamId(),
          dialogParameter.getAvailableGold(),
          dialogParameter.isWizardAvailable()
        ));
        getDialog().showDialog(this);
      } else {
        showStatus("Buy Inducements", "Waiting for coach to buy Inducements.", StatusType.WAITING);
      }
    }

  }

  public void dialogClosed(IDialog pDialog) {
    hideDialog();
    if (testDialogHasId(pDialog, DialogId.BUY_INDUCEMENTS)) {
      DialogBuyInducements buyInducementsDialog = (DialogBuyInducements) pDialog;
      getClient().getCommunication().sendBuyInducements(
      	buyInducementsDialog.getTeamId(),
      	buyInducementsDialog.getAvailableGold(),
      	buyInducementsDialog.getSelectedInducements(),
      	buyInducementsDialog.getSelectedStarPlayerIds(),
      	buyInducementsDialog.getSelectedMercenaryIds(),
      	buyInducementsDialog.getSelectedMercenarySkills()
      );
    }
  }

}