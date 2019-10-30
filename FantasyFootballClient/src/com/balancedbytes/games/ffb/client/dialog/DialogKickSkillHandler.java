package com.balancedbytes.games.ffb.client.dialog;

import java.awt.Color;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogKickSkillParameter;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

/**
 * 
 * @author Kalimar
 */
public class DialogKickSkillHandler extends DialogHandler {
  
  private static final Color _MARKED_FIELDS_COLOR = new Color(1.0f, 1.0f, 1.0f, 0.3f);
  
  public DialogKickSkillHandler(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public void showDialog() {
    
    Game game = getClient().getGame();
    UserInterface userInterface = getClient().getUserInterface();
    DialogKickSkillParameter dialogKickSkillParameter = (DialogKickSkillParameter) game.getDialogParameter();

    if (dialogKickSkillParameter != null) {
   
      Player player = game.getPlayerById(dialogKickSkillParameter.getPlayerId());
      
      if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome().hasPlayer(player)) {

        userInterface.getFieldComponent().getLayerRangeRuler().markCoordinates(new FieldCoordinate[] { dialogKickSkillParameter.getBallCoordinateWithKick(), dialogKickSkillParameter.getBallCoordinate() }, _MARKED_FIELDS_COLOR);
        userInterface.getFieldComponent().refresh();
        
        setDialog(new DialogSkillUse(getClient(), new DialogSkillUseParameter(player.getId(), Skill.KICK, 0))); 
        getDialog().showDialog(this);
        
      } else {
        
        StringBuilder message = new StringBuilder();     
        message.append("Waiting for coach to use ").append(Skill.KICK.getName()).append(".");
        showStatus("Skill Use", message.toString(), StatusType.WAITING);
        
      }
      
    }    
    
  }
  
  
  public void dialogClosed(IDialog pDialog) {
    hideDialog();
    if (testDialogHasId(pDialog, DialogId.SKILL_USE)) {
      UserInterface userInterface = getClient().getUserInterface();
      userInterface.getFieldComponent().getLayerRangeRuler().clearMarkedCoordinates();
      userInterface.getFieldComponent().refresh();
      DialogSkillUse skillUseDialog = (DialogSkillUse) pDialog;
      String playerId = ((DialogKickSkillParameter)getClient().getGame().getDialogParameter()).getPlayerId();
      getClient().getCommunication().sendUseSkill(skillUseDialog.getSkill(), skillUseDialog.isChoiceYes(), playerId);
    }
  }

}
