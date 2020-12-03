package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PushbackMode;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.block.StepPushback;
import com.balancedbytes.games.ffb.server.step.action.block.StepPushback.StepState;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerPushback;
import com.balancedbytes.games.ffb.skill.Pass;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilCards;

public class GrabBehaviour extends SkillBehaviour<Pass> {
  public GrabBehaviour() {
    super(Pass.class);
    
    registerModifier(new StepModifier<StepPushback, StepPushback.StepState>(3) {

      @Override
      public StepCommandStatus handleCommandHook(StepPushback step, StepPushback.StepState state, ClientCommandUseSkill useSkillCommand) {
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
        Skill cancellingSkill = UtilCards.getSkillCancelling(state.defender, skill);
        boolean allowGrabOutsideBlock = UtilCards.hasSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.grabOutsideBlock);
        
        if (((state.grabbing == null) || state.grabbing) && state.freeSquareAroundDefender
            && UtilCards.hasSkill(game, actingPlayer, skill) && attackerCoordinate.isAdjacent(defenderCoordinate)
            && cancellingSkill == null
            && ((actingPlayer.getPlayerAction() == PlayerAction.BLOCK)
                || (actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK)
                || allowGrabOutsideBlock)) {
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
                new DialogSkillUseParameter(actingPlayer.getPlayerId(), skill, 0), false);
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
