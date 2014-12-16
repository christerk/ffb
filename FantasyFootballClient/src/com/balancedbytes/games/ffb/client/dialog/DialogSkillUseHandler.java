package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

/**
 * 
 * @author Kalimar
 */
public class DialogSkillUseHandler extends DialogHandler {
  
  public DialogSkillUseHandler(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public void showDialog() {
    
    Game game = getClient().getGame();
    DialogSkillUseParameter dialogSkillUseParameter = (DialogSkillUseParameter) game.getDialogParameter();

    if (dialogSkillUseParameter != null) {
   
      Player player = game.getPlayerById(dialogSkillUseParameter.getPlayerId());
      
      if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome().hasPlayer(player)) {
        setDialog(new DialogSkillUse(getClient(), dialogSkillUseParameter)); 
        getDialog().showDialog(this);
        if (!game.isHomePlaying()) {
          playSound(SoundId.QUESTION);
        }
        
      } else {
        StringBuilder message = new StringBuilder();     
        String skillName = (dialogSkillUseParameter.getSkill() != null) ? dialogSkillUseParameter.getSkill().getName() : null;
        message.append("Waiting for coach to use ").append(skillName);
        if (dialogSkillUseParameter.getMinimumRoll() > 0) {
          message.append(" (").append(dialogSkillUseParameter.getMinimumRoll()).append("+ to succeed)");
        }
        message.append(".");
        showStatus("Skill Use", message.toString(), StatusType.WAITING);
      }
      
    }    
    
  }
  
  
  public void dialogClosed(IDialog pDialog) {
    hideDialog();
    if (testDialogHasId(pDialog, DialogId.SKILL_USE)) {
      DialogSkillUse skillUseDialog = (DialogSkillUse) pDialog;
      getClient().getCommunication().sendUseSkill(skillUseDialog.getSkill(), skillUseDialog.isChoiceYes());
    }
  }

}
