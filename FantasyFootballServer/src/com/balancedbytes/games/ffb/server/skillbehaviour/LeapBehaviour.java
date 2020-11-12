package com.balancedbytes.games.ffb.server.skillbehaviour;

import java.util.Set;

import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.LeapModifier;
import com.balancedbytes.games.ffb.LeapModifierFactory;
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
import com.balancedbytes.games.ffb.server.step.action.move.StepLeap;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.skill.Leap;
import com.balancedbytes.games.ffb.util.UtilCards;

public class LeapBehaviour extends SkillBehaviour<Leap> {
	public LeapBehaviour() {
		super();

		registerModifier(new StepModifier<StepLeap, StepLeap.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepLeap step,
					com.balancedbytes.games.ffb.server.step.action.move.StepLeap.StepState state,
					ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepLeap step,
					com.balancedbytes.games.ffb.server.step.action.move.StepLeap.StepState state) {
				 Game game = step.getGameState().getGame();
				    ActingPlayer actingPlayer = game.getActingPlayer();
				    boolean doLeap = (actingPlayer.isLeaping() && UtilCards.hasUnusedSkill(game, actingPlayer, skill));
				    if (doLeap) {
				      if (ReRolledAction.LEAP == step.getReRolledAction()) {
				        if ((step.getReRollSource() == null) || !UtilServerReRoll.useReRoll(step, step.getReRollSource(), actingPlayer.getPlayer())) {
				        	step.publishParameter(new StepParameter(StepParameterKey.INJURY_TYPE, InjuryType.DROP_LEAP));
				        	step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
				          doLeap = false;
				        }
				      }
				      if (doLeap) {
				        switch (leap(step)) {
				          case SUCCESS:
				            actingPlayer.setLeaping(false);
				            actingPlayer.markSkillUsed(skill);
				            step.getResult().setNextAction(StepAction.NEXT_STEP);
				            break;
				          case FAILURE:
				            actingPlayer.setLeaping(false);
				            actingPlayer.markSkillUsed(skill);
				            step.publishParameter(new StepParameter(StepParameterKey.INJURY_TYPE, InjuryType.DROP_LEAP));
				          	step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
				            break;
				          default:
				          	break;
				        }
				      }
				    } else {
				    	step.getResult().setNextAction(StepAction.NEXT_STEP);
				    }
				return false;
			}
			
		});
		
	}
	
	private ActionStatus leap(StepLeap step) {
	    ActionStatus status = null;
	    Game game = step.getGameState().getGame();
	    ActingPlayer actingPlayer = game.getActingPlayer();
	    LeapModifierFactory modifierFactory = new LeapModifierFactory();
	    Set<LeapModifier> leapModifiers = modifierFactory.findLeapModifiers(game);
	    int minimumRoll = DiceInterpreter.getInstance().minimumRollLeap(actingPlayer.getPlayer(), leapModifiers);
	    int roll = step.getGameState().getDiceRoller().rollSkill();
	    boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
	    LeapModifier[] leapModifierArray = modifierFactory.toArray(leapModifiers);
	    boolean reRolled = ((step.getReRolledAction() == ReRolledAction.LEAP) && (step.getReRollSource() != null));
	    step.getResult().addReport(new ReportSkillRoll(ReportId.LEAP_ROLL, actingPlayer.getPlayerId(), successful, roll, minimumRoll, reRolled, leapModifierArray));
	    if (successful) {
	      status = ActionStatus.SUCCESS;
	    } else {
	      status = ActionStatus.FAILURE;
	      if (step.getReRolledAction() != ReRolledAction.LEAP) {
	    	  step.setReRolledAction(ReRolledAction.LEAP);
	        if (UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer.getPlayer(), ReRolledAction.LEAP, minimumRoll, false)) {
	          status = ActionStatus.WAITING_FOR_RE_ROLL;
	        }
	      }
	    }
	    return status;
	  }
}