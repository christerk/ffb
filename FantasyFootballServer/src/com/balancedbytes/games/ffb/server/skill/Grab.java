package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PushbackMode;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.server.ServerSkillConstants;
import com.balancedbytes.games.ffb.server.model.CancelSkillProperty;
import com.balancedbytes.games.ffb.server.model.GrabOutsideBlockProperty;
import com.balancedbytes.games.ffb.server.model.ServerSkill;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.block.StepPushback;
import com.balancedbytes.games.ffb.server.step.action.block.StepPushback.StepState;
import com.balancedbytes.games.ffb.server.util.UtilServerCards;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerPushback;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
* A player with this skill uses his great strength and prowess to grab his
* opponent and throw him around. To represent this, only while making a
* Block Action, if his block results in a push back he may choose any
* empty square adjacent to his opponent to push back his opponent. When
* making a Block or Blitz Action, Grab and Side Step will cancel each other
* out and the standard pushback rules apply. Grab will not work if there
* are no empty adjacent squares. A player with the Grab skill can never
* learn or gain the Frenzy skill through any means. Likewise, a player with
* the Frenzy skill can never learn or gain the Grab skill through any
* means.
*/
public class Grab extends ServerSkill {

  public Grab() {
    super("Grab", SkillCategory.STRENGTH);
    
    registerProperty(new CancelSkillProperty(ServerSkillConstants.SIDE_STEP));
    
    registerModifier(new StepModifier<StepPushback, StepPushback.StepState>(3) {

      @Override
      public StepCommandStatus handleCommandHook(StepPushback step, StepPushback.StepState state, NetCommand netCommand) {
        ClientCommandUseSkill useSkillCommand = (ClientCommandUseSkill) netCommand;
        
        state.grabbing = useSkillCommand.isSkillUsed();
        return StepCommandStatus.EXECUTE_STEP;
      }

      @Override
      public boolean handleExecuteStepHook(StepPushback step, StepState state) {
        Game game = step.getGameState().getGame();
        ActingPlayer actingPlayer = game.getActingPlayer();
        FieldModel fieldModel = game.getFieldModel();
        FieldCoordinate attackerCoordinate = fieldModel.getPlayerCoordinate(actingPlayer.getPlayer());
        FieldCoordinate defenderCoordinate = state.startingPushbackSquare.getCoordinate();
        ServerSkill cancellingSkill = UtilServerCards.getSkillCancelling(game, state.defender, Grab.this);
        ServerSkill allowGrabOutsideBlockSkill = UtilServerCards.getSkillWithProperty(game, actingPlayer.getPlayer(), GrabOutsideBlockProperty.class);
        
        if (((state.grabbing == null) || state.grabbing) && state.freeSquareAroundDefender
            && UtilCards.hasSkill(game, actingPlayer, Grab.this) && attackerCoordinate.isAdjacent(defenderCoordinate)
            && cancellingSkill == null
            && ((actingPlayer.getPlayerAction() == PlayerAction.BLOCK)
                || (actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK)
                || allowGrabOutsideBlockSkill != null)) {
          if ((state.grabbing == null) && ArrayTool.isProvided(state.pushbackSquares)) {
            state.grabbing = true;
            for (int i = 0; i < state.pushbackSquares.length; i++) {
              if (fieldModel.getPlayer(state.pushbackSquares[i].getCoordinate()) != null) {
                state.grabbing = null;
                break;
              }
            }
          }
          if (state.grabbing == null) {
            UtilServerDialog.showDialog(step.getGameState(),
                new DialogSkillUseParameter(actingPlayer.getPlayerId(), Grab.this, 0), false);
            state.grabbing = null;
          } else {
            if (state.grabbing) {
              state.pushbackMode = PushbackMode.GRAB;
              for (int i = 0; i < state.pushbackSquares.length; i++) {
                if (!state.pushbackSquares[i].isSelected()) {
                  fieldModel.remove(state.pushbackSquares[i]);
                }
              }
              fieldModel.add(UtilServerPushback.findPushbackSquares(game, state.startingPushbackSquare, state.pushbackMode));
              state.grabbing = null;
            } else {
              state.grabbing = false;
            }
            step.publishParameter(new StepParameter(StepParameterKey.STARTING_PUSHBACK_SQUARE, null));
          }
          return true;
        }
        return false;
      }
      
    });
  }

}
