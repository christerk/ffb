package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.model.ServerSkill;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.block.StepPushback;
import com.balancedbytes.games.ffb.server.util.UtilServerCards;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
* A player with this skill may choose to not be pushed back as the result of
* a block. He may choose to ignore being pushed by "Pushed" results, and
* to have 'Knock-down' results knock the player down in the square where
* he started. If a player is pushed back into a player with using Stand Firm
* then neither player moves.
*/
public class StandFirm extends ServerSkill {

  public StandFirm() {
    super("Stand Firm", SkillCategory.STRENGTH);
    
    registerModifier(new StepModifier<StepPushback, StepPushback.StepState>(1) {

      @Override
      public StepCommandStatus handleCommandHook(StepPushback step, StepPushback.StepState state, NetCommand netCommand) {
        ClientCommandUseSkill useSkillCommand = (ClientCommandUseSkill) netCommand;
        
        state.standingFirm.put(useSkillCommand.getPlayerId(), useSkillCommand.isSkillUsed());
        return StepCommandStatus.EXECUTE_STEP;
      }
      
      @Override
      public boolean handleExecuteStepHook(StepPushback step, StepPushback.StepState state) {
        Game game = step.getGameState().getGame();
        ActingPlayer actingPlayer = game.getActingPlayer();
        
        ServerSkill cancellingSkill = UtilServerCards.getSkillCancelling(game, actingPlayer.getPlayer(), StandFirm.this);
        // handle auto-stand firm
        PlayerState playerState = game.getFieldModel().getPlayerState(state.defender);
        if (playerState.isRooted()) {
          state.standingFirm.put(state.defender.getId(), true);
        } else if (playerState.isProne() || ((state.oldDefenderState != null) && state.oldDefenderState.isProne())) {
          state.standingFirm.put(state.defender.getId(), false);
        } else if ((PlayerAction.BLITZ == actingPlayer.getPlayerAction())
            && cancellingSkill != null
            && game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer())
                .isAdjacent(game.getFieldModel().getPlayerCoordinate(state.defender))) {
          state.standingFirm.put(state.defender.getId(), false);
          step.getResult().addReport(
              new ReportSkillUse(actingPlayer.getPlayerId(), cancellingSkill, true, SkillUse.CANCEL_STAND_FIRM));
        }

        // handle stand firm
        if (UtilCards.hasSkill(game, state.defender, StandFirm.this)
            && state.standingFirm.getOrDefault(state.defender.getId(), true)) {
          if (!state.standingFirm.containsKey(state.defender.getId())) {
            UtilServerDialog.showDialog(step.getGameState(),
                new DialogSkillUseParameter(state.defender.getId(), StandFirm.this, 0), true);
          }
          if (state.standingFirm.containsKey(state.defender.getId())) {
            if (state.standingFirm.containsKey(state.defender.getId())) {
              state.doPush = true;
              state.pushbackStack.clear();
              step.publishParameter(new StepParameter(StepParameterKey.STARTING_PUSHBACK_SQUARE, null));
              step.publishParameter(new StepParameter(StepParameterKey.FOLLOWUP_CHOICE, false));
              step.getResult().addReport(new ReportSkillUse(state.defender.getId(), StandFirm.this, true, SkillUse.AVOID_PUSH));
            } else {
              step.getResult().addReport(new ReportSkillUse(state.defender.getId(), StandFirm.this, false, null));
            }

          }
          return true;
        }
        return false;
      }
    });
  }

}
