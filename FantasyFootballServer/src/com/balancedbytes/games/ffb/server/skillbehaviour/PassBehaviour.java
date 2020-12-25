package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.ReRollSources;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportPassRoll;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.pass.StepHailMaryPass;
import com.balancedbytes.games.ffb.server.step.action.pass.StepPass;
import com.balancedbytes.games.ffb.server.step.action.pass.StepPass.StepState;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.skill.Pass;
import com.balancedbytes.games.ffb.util.UtilCards;

public class PassBehaviour extends SkillBehaviour<Pass> {
	public PassBehaviour() {
		super();

		registerModifier(new StepModifier<StepPass, StepPass.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepPass step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				step.setReRolledAction(ReRolledActions.PASS);
				step.setReRollSource(useSkillCommand.isSkillUsed() ? ReRollSources.PASS : null);
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepPass step, StepState state) {
				// TODO Auto-generated method stub
				return false;
			}

		});

		registerModifier(new StepModifier<StepHailMaryPass, StepHailMaryPass.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepHailMaryPass step,
					com.balancedbytes.games.ffb.server.step.action.pass.StepHailMaryPass.StepState state,
					ClientCommandUseSkill useSkillCommand) {
				step.setReRolledAction(ReRolledActions.PASS);
				step.setReRollSource(useSkillCommand.isSkillUsed() ? ReRollSources.PASS : null);
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepHailMaryPass step,
					com.balancedbytes.games.ffb.server.step.action.pass.StepHailMaryPass.StepState state) {
				Game game = step.getGameState().getGame();
				UtilServerDialog.hideDialog(step.getGameState());
				if (game.getThrower() == null) {
					return false;
				}
				if (PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction()) {
					game.getFieldModel().setBombMoving(true);
				} else {
					game.getFieldModel().setBallMoving(true);
				}
				boolean doRoll = true;
				boolean doNextStep = false;
				if (ReRolledActions.PASS == step.getReRolledAction()) {
					if ((step.getReRollSource() == null)
							|| !UtilServerReRoll.useReRoll(step, step.getReRollSource(), game.getThrower())) {
						doRoll = false;
						doNextStep = true;
					}
				}
				if (doRoll) {
					int roll = step.getGameState().getDiceRoller().rollSkill();
					state.passFumble = (roll == 1);
					boolean reRolled = ((step.getReRolledAction() == ReRolledActions.PASS) && (step.getReRollSource() != null));
					step.getResult().addReport(new ReportPassRoll(game.getThrowerId(), state.passFumble, roll, reRolled,
							(PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction())));
					doNextStep = true;
					if (state.passFumble) {
						if (step.getReRolledAction() != ReRolledActions.PASS) {
							step.setReRolledAction(ReRolledActions.PASS);
							if (UtilCards.hasSkill(game, game.getThrower(), skill) && !state.passSkillUsed) {
								doNextStep = false;
								state.passSkillUsed = true;
								Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
								UtilServerDialog.showDialog(step.getGameState(),
										new DialogSkillUseParameter(game.getThrowerId(), skill, 2),
										actingTeam.hasPlayer(game.getThrower()));
							} else {
								if (UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), game.getThrower(),
										ReRolledActions.PASS, 2, false)) {
									doNextStep = false;
								}
							}
						}
					}
				}
				if (doNextStep) {
					step.publishParameter(new StepParameter(StepParameterKey.PASS_FUMBLE, state.passFumble));
					if (state.passFumble) {
						if (PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction()) {
							game.getFieldModel().setBombCoordinate(game.getFieldModel().getPlayerCoordinate(game.getThrower()));
						} else {
							game.getFieldModel().setBallCoordinate(game.getFieldModel().getPlayerCoordinate(game.getThrower()));
							step.publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE,
									CatchScatterThrowInMode.SCATTER_BALL));
						}
						step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
					} else {
						if (PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction()) {
							game.getFieldModel().setBombCoordinate(game.getFieldModel().getPlayerCoordinate(game.getThrower()));
							game.getFieldModel().setBombMoving(false);
						} else {
							game.getFieldModel().setBallCoordinate(game.getPassCoordinate());
						}
						step.getResult().setNextAction(StepAction.NEXT_STEP);
					}
				}
				return false;
			}

		});
	}
}