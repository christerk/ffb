package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.dialog.DialogBloodlustActionParameter;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportBloodLustRoll;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.bb2020.StepBloodLust;
import com.fumbbl.ffb.server.step.bb2020.StepBloodLust.StepState;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.skill.bb2020.Bloodlust;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Arrays;

import static com.fumbbl.ffb.PlayerAction.*;

@RulesCollection(Rules.BB2020)
public class BloodLustBehaviour extends SkillBehaviour<Bloodlust> {
	public BloodLustBehaviour() {
		super();

		registerModifier(new StepModifier<StepBloodLust, StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepBloodLust step, StepState state,
																								 ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepBloodLust step, StepState state) {

				Game game = step.getGameState().getGame();
				if (state.status == ActionStatus.WAIT_FOR_ACTION_CHANGE) {
					step.publishParameter(StepParameter.from(StepParameterKey.BLOOD_LUST_ACTION, state.bloodlustAction));
					step.publishParameter(new StepParameter(StepParameterKey.MOVE_STACK, null));
					if (StringTool.isProvided(state.goToLabelOnFailure)
						&& (state.bloodlustAction != null || game.getActingPlayer().getPlayerAction().isPassing())) {
						step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
					} else {
						step.getResult().setNextAction(StepAction.NEXT_STEP);
					}
					return false;
				}

				ActionStatus status = ActionStatus.SUCCESS;
				if (!game.getTurnMode().checkNegatraits()) {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
					return false;
				}
				ActingPlayer actingPlayer = game.getActingPlayer();
				boolean doRoll = true;
				if (ReRolledActions.BLOOD_LUST == step.getReRolledAction()) {
					if ((step.getReRollSource() == null)
						|| !UtilServerReRoll.useReRoll(step, step.getReRollSource(), actingPlayer.getPlayer())) {
						doRoll = false;
						status = ActionStatus.FAILURE;
						actingPlayer.setSufferingBloodLust(true);
					}
				} else {
					doRoll = UtilCards.hasUnusedSkill(actingPlayer, skill);
				}
				if (doRoll) {
					step.commitTargetSelection();
					PlayerAction playerAction = actingPlayer.getPlayerAction();
					boolean goodConditions = ((playerAction == PlayerAction.BLITZ_MOVE)
						|| (playerAction != null && playerAction.isKickingDowned())
						|| (playerAction == PlayerAction.BLITZ)
						|| (playerAction == BLOCK)
						|| (playerAction == PlayerAction.MULTIPLE_BLOCK)
						|| (playerAction == PlayerAction.STAND_UP_BLITZ));
					int roll = step.getGameState().getDiceRoller().rollSkill();
					int minimumRoll = Math.max(2, actingPlayer.getPlayer().getSkillIntValue(skill) - (goodConditions ? 1 : 0));
					boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
					actingPlayer.markSkillUsed(skill);
					if (!successful) {
						status = ActionStatus.FAILURE;
						if ((ReRolledActions.BLOOD_LUST != step.getReRolledAction()) && UtilServerReRoll.askForReRollIfAvailable(
							step.getGameState(), actingPlayer.getPlayer(), ReRolledActions.BLOOD_LUST, minimumRoll, false)) {
							status = ActionStatus.WAITING_FOR_RE_ROLL;
						} else {
							actingPlayer.setSufferingBloodLust(true);
						}
					}
					boolean reRolled = ((ReRolledActions.BLOOD_LUST == step.getReRolledAction())
						&& (step.getReRollSource() != null));
					step.getResult().addReport(new ReportBloodLustRoll(actingPlayer.getPlayerId(),
						successful, roll, minimumRoll, reRolled, null));
				}
				if (status == ActionStatus.SUCCESS) {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				}
				if (status == ActionStatus.FAILURE) {
					if (Arrays.asList(new PlayerAction[]
							{BLOCK, PASS, HAND_OVER, THROW_BOMB, THROW_TEAM_MATE, KICK_TEAM_MATE, FOUL, STAND_UP, STAND_UP_BLITZ}
						)
						.contains(actingPlayer.getPlayerAction())) {
						boolean changeToMove = Arrays.asList(new PlayerAction[]{BLOCK, THROW_BOMB, STAND_UP}).contains(actingPlayer.getPlayerAction());

						UtilServerDialog.showDialog(step.getGameState(), new DialogBloodlustActionParameter(changeToMove), false);
						step.getResult().setNextAction(StepAction.CONTINUE);
						state.status = ActionStatus.WAIT_FOR_ACTION_CHANGE;
					} else {
						step.publishParameter(new StepParameter(StepParameterKey.MOVE_STACK, null));
						step.getResult().setNextAction(StepAction.NEXT_STEP);
					}
				}
				return false;
			}
		});
	}
}