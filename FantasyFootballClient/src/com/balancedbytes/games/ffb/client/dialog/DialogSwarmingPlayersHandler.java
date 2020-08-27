package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.dialog.DialogSwarmingPlayersParameter;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class DialogSwarmingPlayersHandler extends DialogHandler {

  public DialogSwarmingPlayersHandler(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public void showDialog() {
    
    Game game = getClient().getGame();

    int amount = ((DialogSwarmingPlayersParameter)game.getDialogParameter()).getAmount();

    if ((ClientMode.PLAYER == getClient().getMode()) && game.isHomePlaying()) {
      setDialog(new DialogInformation(getClient(), "Place swarming players", new String[] { "You may place up to " +
        amount + " players with the Swarming skill in your half. They cannot be placed at the Line of Scrimmage or in the wide zones." },
        DialogInformation.OK_DIALOG, IIconProperty.GAME_REF));
      getDialog().showDialog(this);
        
    } else {
      showStatus("Skill Use", "Waiting for coach to place swarming players.", StatusType.WAITING);
    }    
    
  }
  
  public void dialogClosed(IDialog pDialog) {
    hideDialog();
  }

}
