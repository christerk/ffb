package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.ReRolledActionFactory;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportConfusionRoll;
import com.balancedbytes.games.ffb.server.ActionStatus;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.action.common.StepTakeRoot;
import com.balancedbytes.games.ffb.server.step.action.common.StepTakeRoot.StepState;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.skill.TakeRoot;
import com.balancedbytes.games.ffb.util.UtilCards;

public class TakeRootBehaviour extends SkillBehaviour<TakeRoot> {
  public TakeRootBehaviour() {
    super();
    
    registerModifier(new StepModifier<StepTakeRoot, StepTakeRoot.StepState>() {

      @Override
      public StepCommandStatus handleCommandHook(StepTakeRoot step, StepState state,
          ClientCommandUseSkill useSkillCommand) {
        return null;
      }

      @Override
      public boolean handleExecuteStepHook(StepTakeRoot step, StepTakeRoot.StepState state) {
        Game game = step.getGameState().getGame();
        ActingPlayer actingPlayer = game.getActingPlayer();
        PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());

        if (!playerState.isRooted()) {
          boolean doRoll = true;
          ReRolledAction reRolledAction = new ReRolledActionFactory().forSkill(skill); 
          if ((reRolledAction != null) && (reRolledAction == step.getReRolledAction())) {
            if ((step.getReRollSource() == null) || !UtilServerReRoll.useReRoll(step, step.getReRollSource(), actingPlayer.getPlayer())) {
              doRoll = false;
              state.status = ActionStatus.FAILURE;
              state.continueOnFailure = step.cancelPlayerAction();
            }
          } else {
            doRoll = UtilCards.hasUnusedSkill(game, actingPlayer, skill);
          }
          if (doRoll) {
            int roll = step.getGameState().getDiceRoller().rollSkill();
            int minimumRoll = DiceInterpreter.getInstance().minimumRollConfusion(true);
            boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
            actingPlayer.markSkillUsed(skill);
            if (!successful) {
              state.status = ActionStatus.FAILURE;
              if (((reRolledAction == null) || (reRolledAction != step.getReRolledAction())) && UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer.getPlayer(), reRolledAction, minimumRoll, false)) {
                state.status = ActionStatus.WAITING_FOR_RE_ROLL;
              } else {
                state.continueOnFailure = step.cancelPlayerAction();
              }
            }
            boolean reRolled = ((reRolledAction != null) && (reRolledAction == step.getReRolledAction()) && (step.getReRollSource() != null));
            step.getResult().addReport(new ReportConfusionRoll(actingPlayer.getPlayerId(), successful, roll, minimumRoll, reRolled, skill));
          }
        }
        
        return false;
      }
      
    });
  }
}
