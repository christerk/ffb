package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.server.ActionStatus;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.common.StepBloodLust;
import com.balancedbytes.games.ffb.server.step.action.common.StepBloodLust.StepState;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.skill.BloodLust;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;

public class BloodLustBehaviour extends SkillBehaviour<BloodLust> {
	public BloodLustBehaviour() {
		super();
		
		registerModifier(new StepModifier<StepBloodLust, StepBloodLust.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepBloodLust step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepBloodLust step, StepState state) {
				 ActionStatus status = ActionStatus.SUCCESS;
				    Game game = step.getGameState().getGame();
				    if (!game.getTurnMode().checkNegatraits()) {
				    	step.getResult().setNextAction(StepAction.NEXT_STEP);
				    	return false;
				    }
				    ActingPlayer actingPlayer = game.getActingPlayer();
					  boolean doRoll = true;
					  if (ReRolledAction.BLOOD_LUST == step.getReRolledAction()) {
					    if ((step.getReRollSource() == null) || !UtilServerReRoll.useReRoll(step, step.getReRollSource(), actingPlayer.getPlayer())) {
					      doRoll = false;
					      status = ActionStatus.FAILURE;
					      actingPlayer.setSufferingBloodLust(true);
					    }
					  } else {
					    doRoll = UtilCards.hasUnusedSkill(game, actingPlayer, skill);
					  }
				    if (doRoll) {
				      int roll = step.getGameState().getDiceRoller().rollSkill();
				      int minimumRoll = DiceInterpreter.getInstance().minimumRollBloodLust();
				      boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
				      actingPlayer.markSkillUsed(skill);
				      if (!successful) {
				        status = ActionStatus.FAILURE;
				        if ((ReRolledAction.BLOOD_LUST != step.getReRolledAction()) && UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer.getPlayer(), ReRolledAction.BLOOD_LUST, minimumRoll, false)) {
				          status = ActionStatus.WAITING_FOR_RE_ROLL;
				        } else {
				          actingPlayer.setSufferingBloodLust(true);
				        }
				      }
				      boolean reRolled = ((ReRolledAction.BLOOD_LUST == step.getReRolledAction()) && (step.getReRollSource() != null));
				      step.getResult().addReport(new ReportSkillRoll(ReportId.BLOOD_LUST_ROLL, actingPlayer.getPlayerId(), successful, roll, minimumRoll, reRolled));
				    }
				    if (status == ActionStatus.SUCCESS) {
				    	step.getResult().setNextAction(StepAction.NEXT_STEP);
				    }
				    if (status == ActionStatus.FAILURE) {
				    	step.publishParameter(new StepParameter(StepParameterKey.MOVE_STACK, null));
				    	if (StringTool.isProvided(state.goToLabelOnFailure)) {
				    		step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
				    	} else {
				    		step.getResult().setNextAction(StepAction.NEXT_STEP);    		
				    	}
				    }
				return false;
			}
		});
	}	
}