package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class DialogCoinChoiceHandler extends DialogHandler {
  
  public DialogCoinChoiceHandler(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public void showDialog() {
    Game game = getClient().getGame();
    if ((ClientMode.PLAYER == getClient().getMode()) && game.isHomePlaying()) {
      setDialog(new DialogCoinChoice(getClient()));
      getDialog().showDialog(this);
    } else {
      showStatus("Coin Throw", "Waiting for coach to choose heads or tails.", StatusType.WAITING);
    }
  }
  
  public void dialogClosed(IDialog pDialog) {
    hideDialog();
    if (testDialogHasId(pDialog, DialogId.COIN_CHOICE)) {
      DialogCoinChoice coinThrowDialog = (DialogCoinChoice) pDialog;
      getClient().getCommunication().sendCoinChoice(coinThrowDialog.isChoiceHeads());
    }
  }
  
}
