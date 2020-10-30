package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class DialogWizardSpellHandler extends DialogHandler {
  
  public DialogWizardSpellHandler(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public void showDialog() {
    
    Game game = getClient().getGame();

    if ((ClientMode.PLAYER == getClient().getMode()) && game.isHomePlaying()) {
      setDialog(new DialogWizardSpell(getClient()));
      getDialog().showDialog(this);

    } else {
      showStatus("Wizard Spell", "Waiting for coach to select a spell.", StatusType.WAITING);
    }
    
  }
  
  public void dialogClosed(IDialog pDialog) {
    hideDialog();
    if ((pDialog != null) && (pDialog.getId() == DialogId.WIZARD_SPELL)) {
    	SpecialEffect wizardSpell = ((DialogWizardSpell) pDialog).getWizardSpell();
    	if (wizardSpell != null) {
    		getClient().getClientData().setWizardSpell(wizardSpell);
    	} else {
    		getClient().getCommunication().sendWizardSpell(null, null);  // signal cancel
    	}
    }
  }  

}
