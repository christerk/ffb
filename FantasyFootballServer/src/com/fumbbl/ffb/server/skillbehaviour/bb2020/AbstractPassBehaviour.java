package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.PassModifierFactory;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassMechanic;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.PassContext;
import com.fumbbl.ffb.modifiers.PassModifier;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportPassRoll;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.action.pass.StepHailMaryPass;
import com.fumbbl.ffb.server.step.bb2020.pass.StepPass;
import com.fumbbl.ffb.server.step.bb2020.pass.state.PassState;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Set;

import static com.fumbbl.ffb.server.step.StepParameter.from;

public abstract class AbstractPassBehaviour<T extends Skill> extends SkillBehaviour<T> {
	public AbstractPassBehaviour() {
		super();

		registerModifier(new StepModifier<StepPass, PassState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepPass step, PassState state,
			                                           ClientCommandUseSkill useSkillCommand) {
				step.setReRolledAction(ReRolledActions.PASS);
				step.setReRollSource(useSkillCommand.isSkillUsed() ? getReRollSource() : null);
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepPass step, PassState state) {
				return false;
			}

		});

		registerModifier(new StepModifier<StepHailMaryPass, StepHailMaryPass.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepHailMaryPass step,
			                                           StepHailMaryPass.StepState state,
			                                           ClientCommandUseSkill useSkillCommand) {
				step.setReRolledAction(ReRolledActions.PASS);
				step.setReRollSource(useSkillCommand.isSkillUsed() ? getReRollSource() : null);
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
				PassState passState;
				if (step.getGameState().getPassState() == null) {
					step.getGameState().setPassState(new PassState());
				}
				passState = step.getGameState().getPassState();
				passState.setThrowerCoordinate(game.getFieldModel().getPlayerCoordinate(game.getThrower()));

				boolean bombAction;
				if (PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction()) {
					game.getFieldModel().setBombMoving(true);
					bombAction = true;
					if (!StringTool.isProvided(passState.getOriginalBombardier())) {
						passState.setOriginalBombardier(game.getThrowerId());
					}
				} else {
					game.getFieldModel().setBallMoving(true);
					bombAction = false;
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
					PassMechanic mechanic = (PassMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.PASS.name());
					int roll = step.getGameState().getDiceRoller().rollSkill();
					PassModifierFactory factory = game.getFactory(FactoryType.Factory.PASS_MODIFIER);
					PassingDistance passingDistance = PassingDistance.LONG_BOMB;
					PassContext passContext = new PassContext(game, game.getThrower(), passingDistance, bombAction);
					Set<PassModifier> modifiers = factory.findModifiers(passContext);
					PassResult result = mechanic.evaluatePass(game.getThrower(), roll, passingDistance, modifiers, bombAction);
					int minimumRoll = Math.max(2, 2 + passingDistance.getModifier2020() + modifiers.stream().mapToInt(PassModifier::getModifier).sum());
					state.result = result == PassResult.ACCURATE ? PassResult.INACCURATE : result;
					passState.setResult(state.result);
					boolean reRolled = ((step.getReRolledAction() == ReRolledActions.PASS) && (step.getReRollSource() != null));

					step.getResult().addReport(new ReportPassRoll(game.getThrowerId(), roll, minimumRoll, reRolled,
						modifiers.toArray(new PassModifier[0]), passingDistance, bombAction, state.result, true));
					doNextStep = true;
					if (PassResult.FUMBLE == state.result || PassResult.WILDLY_INACCURATE == state.result) {
						if (step.getReRolledAction() != ReRolledActions.PASS) {
							step.setReRolledAction(ReRolledActions.PASS);
							if (UtilCards.hasSkill(game.getThrower(), skill) && !state.passSkillUsed) {
								doNextStep = false;
								state.passSkillUsed = true;
								passState.setPassSkillUsed(true);
								Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
								UtilServerDialog.showDialog(step.getGameState(),
									new DialogSkillUseParameter(game.getThrowerId(), skill, minimumRoll),
									actingTeam.hasPlayer(game.getThrower()));
							} else if (PassResult.SAVED_FUMBLE == state.result) {
							if (PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction()) {
								game.getFieldModel().setBombCoordinate(null);
								game.getFieldModel().setBombMoving(false);
								step.publishParameter(from(StepParameterKey.CATCHER_ID, null));
								step.publishParameter(from(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, null));
								step.publishParameter(new StepParameter(StepParameterKey.DONT_DROP_FUMBLE, true));
							} else {
								game.getFieldModel().setBallCoordinate(game.getFieldModel().getPlayerCoordinate(game.getThrower()));
								game.getFieldModel().setBallMoving(false);
							}
							step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
						} else {
								if (!reRolled && UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), game.getThrower(),
									ReRolledActions.PASS, minimumRoll, false)) {
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

	protected abstract ReRollSource getReRollSource();
}