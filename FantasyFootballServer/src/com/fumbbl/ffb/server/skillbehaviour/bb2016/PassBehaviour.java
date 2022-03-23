package com.fumbbl.ffb.server.skillbehaviour.bb2016;

import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.bb2016.ReportPassRoll;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.bb2016.pass.StepHailMaryPass;
import com.fumbbl.ffb.server.step.bb2016.pass.StepPass;
import com.fumbbl.ffb.server.step.bb2016.pass.StepPass.StepState;
import com.fumbbl.ffb.server.step.bb2016.ttm.StepThrowTeamMate;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.skill.Pass;
import com.fumbbl.ffb.util.UtilCards;

@RulesCollection(Rules.BB2016)
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
				return false;
			}

		});

		registerModifier(new StepModifier<StepThrowTeamMate, StepThrowTeamMate.StepState>() {
			@Override
			public StepCommandStatus handleCommandHook(StepThrowTeamMate step, StepThrowTeamMate.StepState state, ClientCommandUseSkill useSkillCommand) {
				step.setReRolledAction(ReRolledActions.THROW_TEAM_MATE);
				step.setReRollSource(useSkillCommand.isSkillUsed() ? ReRollSources.PASS : null);
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepThrowTeamMate step, StepThrowTeamMate.StepState state) {
				return false;
			}
		});

		registerModifier(new StepModifier<StepHailMaryPass, StepHailMaryPass.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepHailMaryPass step,
																								 StepHailMaryPass.StepState state,
																								 ClientCommandUseSkill useSkillCommand) {
				step.setReRolledAction(ReRolledActions.PASS);
				step.setReRollSource(useSkillCommand.isSkillUsed() ? ReRollSources.PASS : null);
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepHailMaryPass step,
																					 StepHailMaryPass.StepState state) {
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
					state.result = (roll == 1) ? PassResult.FUMBLE : PassResult.INACCURATE;
					boolean reRolled = ((step.getReRolledAction() == ReRolledActions.PASS) && (step.getReRollSource() != null));
					step.getResult().addReport(new ReportPassRoll(game.getThrowerId(), roll, reRolled,
						(PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction()), state.result));
					doNextStep = true;
					if (PassResult.FUMBLE == state.result) {
						if (step.getReRolledAction() != ReRolledActions.PASS) {
							step.setReRolledAction(ReRolledActions.PASS);
							if (UtilCards.hasSkill(game.getThrower(), skill) && !state.passSkillUsed) {
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
					step.publishParameter(new StepParameter(StepParameterKey.PASS_FUMBLE, PassResult.FUMBLE == state.result));
					if (PassResult.FUMBLE == state.result) {
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