package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.ReRolledActionFactory;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportConfusionRoll;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.bb2020.StepUnchannelledFury;
import com.fumbbl.ffb.server.step.bb2020.StepUnchannelledFury.StepState;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.skill.bb2020.UnchannelledFury;
import com.fumbbl.ffb.util.UtilCards;

@RulesCollection(Rules.BB2020)
public class UnchannelledFuryBehaviour extends SkillBehaviour<UnchannelledFury> {
	public UnchannelledFuryBehaviour() {
		super();

		registerModifier(new StepModifier<StepUnchannelledFury, StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepUnchannelledFury step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepUnchannelledFury step, StepState state) {

				ActionStatus status = ActionStatus.SUCCESS;
				Game game = step.getGameState().getGame();
				if (!game.getTurnMode().checkNegatraits()) {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
					return false;
				}
				ActingPlayer actingPlayer = game.getActingPlayer();

				if (state.status == ActionStatus.SKILL_CHOICE_YES) {
					actingPlayer.markSkillUsed(NamedProperties.canPerformTwoBlocksAfterFailedFury);
					step.publishParameter(StepParameter.from(StepParameterKey.ALLOW_SECOND_BLOCK_ACTION, true));
					step.getResult().setNextAction(StepAction.NEXT_STEP);
					return false;
				} else if (state.status == ActionStatus.SKILL_CHOICE_NO) {
					cancelPlayerAction(step);
					failed(step, state);
					return false;
				}

				if (UtilCards.hasSkill(actingPlayer, skill)) {
					boolean doRoll = true;
					ReRolledAction reRolledAction = new ReRolledActionFactory().forSkill(game, skill);
					if ((reRolledAction != null) && (reRolledAction == step.getReRolledAction())) {
						if ((step.getReRollSource() == null)
							|| !UtilServerReRoll.useReRoll(step, step.getReRollSource(), actingPlayer.getPlayer())) {
							doRoll = false;
							Skill furySkill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canPerformTwoBlocksAfterFailedFury);
							if (furySkill != null && actingPlayer.getPlayerAction() == PlayerAction.BLOCK) {
								UtilServerDialog.showDialog(step.getGameState(), new DialogSkillUseParameter(actingPlayer.getPlayerId(), furySkill, 0), true);
								return false;
							} else {
								status = ActionStatus.FAILURE;
								cancelPlayerAction(step);
							}
						}
					} else {
						doRoll = UtilCards.hasUnusedSkill(actingPlayer, skill);
					}
					if (doRoll) {
						step.commitTargetSelection();
						int roll = step.getGameState().getDiceRoller().rollSkill();
						boolean goodConditions = ((actingPlayer.getPlayerAction() == PlayerAction.BLITZ_MOVE)
								|| (actingPlayer.getPlayerAction() == PlayerAction.BLITZ)
								|| (actingPlayer.getPlayerAction() == PlayerAction.BLOCK)
								|| (actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK)
								|| (actingPlayer.getPlayerAction() == PlayerAction.STAND_UP_BLITZ));
						int minimumRoll = DiceInterpreter.getInstance().minimumRollConfusion(goodConditions);
						boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
						actingPlayer.markSkillUsed(skill);
						if (!successful) {
							status = ActionStatus.FAILURE;
							if (((reRolledAction == null) || (reRolledAction != step.getReRolledAction()))
								&& UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer.getPlayer(),
								reRolledAction, minimumRoll, false)) {
								status = ActionStatus.WAITING_FOR_RE_ROLL;
							} else {
								Skill furySkill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canPerformTwoBlocksAfterFailedFury);
								if (furySkill != null && actingPlayer.getPlayerAction() == PlayerAction.BLOCK) {
									UtilServerDialog.showDialog(step.getGameState(), new DialogSkillUseParameter(actingPlayer.getPlayerId(), furySkill, 0), true);
									status = ActionStatus.WAITING_FOR_SKILL_USE;
								} else {
									cancelPlayerAction(step);
								}
							}
						}
						boolean reRolled = ((reRolledAction != null) && (reRolledAction == step.getReRolledAction())
								&& (step.getReRollSource() != null));
						step.getResult().addReport(
								new ReportConfusionRoll(actingPlayer.getPlayerId(), successful, roll, minimumRoll, reRolled, skill));
					}
				}
				if (status == ActionStatus.SUCCESS) {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				} else {
					if (status == ActionStatus.FAILURE) {
						failed(step, state);
					}
				}

				return false;
			}

			private void failed(StepUnchannelledFury step, StepState state) {
				step.publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
				step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
			}
		});
	}

	private void cancelPlayerAction(StepUnchannelledFury step) {
		Game game = step.getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		switch (actingPlayer.getPlayerAction()) {
			case BLITZ:
			case BLITZ_MOVE:
				game.getTurnData().setBlitzUsed(true);
				break;
			case KICK_TEAM_MATE:
			case KICK_TEAM_MATE_MOVE:
				game.getTurnData().setKtmUsed(true);
				break;
			case PASS:
			case PASS_MOVE:
			case THROW_TEAM_MATE:
			case THROW_TEAM_MATE_MOVE:
				game.getTurnData().setPassUsed(true);
				break;
			case HAND_OVER:
			case HAND_OVER_MOVE:
				game.getTurnData().setHandOverUsed(true);
				break;
			case FOUL:
			case FOUL_MOVE:
				if (!actingPlayer.getPlayer().hasSkillProperty(NamedProperties.allowsAdditionalFoul)) {
					game.getTurnData().setFoulUsed(true);
				}
				break;
			default:
				break;
		}
		PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
		if (actingPlayer.isStandingUp()) {
			game.getFieldModel().setPlayerState(actingPlayer.getPlayer(),
				playerState.changeBase(PlayerState.PRONE).changeActive(false));
		} else {
			game.getFieldModel().setPlayerState(actingPlayer.getPlayer(),
				playerState.changeBase(PlayerState.STANDING).changeActive(false));
		}
		game.setPassCoordinate(null);
		step.getResult().setSound(SoundId.ROAR);
		TargetSelectionState targetSelectionState = game.getFieldModel().getTargetSelectionState();
		if (targetSelectionState != null) {
			targetSelectionState.failed();
		}
	}

}