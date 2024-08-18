package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.PassModifierFactory;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassMechanic;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.PassContext;
import com.fumbbl.ffb.modifiers.PassModifier;
import com.fumbbl.ffb.modifiers.StatBasedRollModifier;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.report.bb2020.ReportModifiedPassResult;
import com.fumbbl.ffb.report.bb2020.ReportPassRoll;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.bb2020.pass.StepHailMaryPass;
import com.fumbbl.ffb.server.step.bb2020.pass.state.PassState;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.skill.Pass;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Optional;
import java.util.Set;

import static com.fumbbl.ffb.server.step.StepParameter.from;

@RulesCollection(Rules.BB2020)
public class PassBehaviour extends AbstractPassBehaviour<Pass> {
	@Override
	protected ReRollSource getReRollSource() {
		return ReRollSources.PASS;
	}

	public PassBehaviour() {
		super();
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

				PassMechanic mechanic = (PassMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.PASS.name());
				PassingDistance passingDistance = PassingDistance.LONG_BOMB;
				PassModifierFactory factory = game.getFactory(FactoryType.Factory.PASS_MODIFIER);
				PassContext passContext = new PassContext(game, game.getThrower(), passingDistance, false);
				Set<PassModifier> modifiers = factory.findModifiers(passContext);

				if (ReRolledActions.PASS == step.getReRolledAction()) {
					if (state.usingModifyingSkill == null || !state.usingModifyingSkill) {
						if (step.getReRollSource() == null) {
							doRoll = false;
							doNextStep = true;
						} else if (!UtilServerReRoll.useReRoll(step, step.getReRollSource(), game.getThrower())) {
							if (state.usingModifyingSkill != null || !showUseModifyingSkillDialog(step, state, mechanic,
								passingDistance, modifiers, bombAction)) {
								doRoll = false;
								doNextStep = true;
							}
						}
					}
				}
				if (doRoll) {

					StatBasedRollModifier statBasedRollModifier = null;

					if (state.usingModifyingSkill != null && state.usingModifyingSkill) {
						if (state.minimumRoll == 0) {
							Optional<Integer> minimumRollO = mechanic.minimumRoll(game.getThrower(), passingDistance, modifiers);
							state.minimumRoll = minimumRollO.orElse(0);
						}
						if (state.roll == 0) {
							state.roll = state.minimumRoll > 0 ? step.getGameState().getDiceRoller().rollSkill() : 0;
						}
						Skill modifyingSkill = game.getActingPlayer().getPlayer().getSkillWithProperty(NamedProperties.canAddStrengthToPass);
						step.getResult().addReport(new ReportSkillUse(game.getThrowerId(), modifyingSkill, true, SkillUse.ADD_STRENGTH_TO_ROLL));
						statBasedRollModifier = game.getActingPlayer().statBasedModifier(NamedProperties.canAddStrengthToPass);
						state.result = mechanic.evaluatePass(game.getThrower(), state.roll, passingDistance, modifiers, bombAction, statBasedRollModifier);
						game.getActingPlayer().markSkillUsed(modifyingSkill);
					} else {
						state.roll = step.getGameState().getDiceRoller().rollSkill();
						PassResult result = mechanic.evaluatePass(game.getThrower(), state.roll, passingDistance, modifiers, bombAction);
						state.minimumRoll = Math.max(2, 2 + passingDistance.getModifier2020() + modifiers.stream().mapToInt(PassModifier::getModifier).sum());
						state.result = result == PassResult.ACCURATE ? PassResult.INACCURATE : result;
					}

					passState.setResult(state.result);

					boolean reRolled = ((step.getReRolledAction() == ReRolledActions.PASS) && (step.getReRollSource() != null));

					step.getResult().addReport(new ReportPassRoll(game.getThrowerId(), state.roll, state.minimumRoll, reRolled,
						modifiers.toArray(new PassModifier[0]), passingDistance, bombAction, state.result, true, statBasedRollModifier));
					doNextStep = true;
					if (PassResult.FUMBLE == state.result || PassResult.WILDLY_INACCURATE == state.result || PassResult.SAVED_FUMBLE == state.result) {
						if (step.getReRolledAction() != ReRolledActions.PASS) {
							step.setReRolledAction(ReRolledActions.PASS);
							Skill modificationSkill = getModifyingSkill(step, state, mechanic, passingDistance, modifiers, bombAction);
							ReRollSource passingReroll = UtilCards.getRerollSource(game.getThrower(), ReRolledActions.PASS);

							if (passingReroll != null && !state.passSkillUsed) {
								doNextStep = false;
								state.passSkillUsed = true;
								passState.setPassSkillUsed(true);
								Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
								UtilServerDialog.showDialog(step.getGameState(),
									new DialogSkillUseParameter(game.getThrowerId(), passingReroll.getSkill(game), state.minimumRoll, modificationSkill),
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
								if (!reRolled && UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), game.getActingPlayer(),
									ReRolledActions.PASS, state.minimumRoll, false, modificationSkill)) {
									doNextStep = false;
								}
							}
						} else if (state.usingModifyingSkill == null && showUseModifyingSkillDialog(step, state, mechanic,
							passingDistance, modifiers, bombAction)) {
							doNextStep = false;
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

			private boolean showUseModifyingSkillDialog(StepHailMaryPass step, StepHailMaryPass.StepState state,
																									PassMechanic mechanic, PassingDistance passingDistance, Set<PassModifier> passModifiers, boolean isBomb) {
				if (state.usingModifyingSkill == null) {
					Skill modifyingSkill = getModifyingSkill(step, state, mechanic, passingDistance, passModifiers, isBomb);
					if (modifyingSkill != null) {
						UtilServerDialog.showDialog(step.getGameState(),
							new DialogSkillUseParameter(step.getGameState().getGame().getThrowerId(), modifyingSkill, 0), false);
						return true;
					}
				}
				return false;
			}

			private Skill getModifyingSkill(StepHailMaryPass step, StepHailMaryPass.StepState state,
																			PassMechanic mechanic, PassingDistance passingDistance, Set<PassModifier> passModifiers, boolean isBomb) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				Skill modificationSkill = null;
				if (game.getThrowerId().equals(actingPlayer.getPlayerId())) {
					PassResult modifiedResult = mechanic.evaluatePass(game.getThrower(), state.roll, passingDistance, passModifiers, isBomb, actingPlayer.statBasedModifier(NamedProperties.canAddStrengthToPass));
					if (state.result != modifiedResult) {
						modificationSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canAddStrengthToPass);
						step.getResult().addReport(new ReportModifiedPassResult(modificationSkill, modifiedResult));
					}
				}
				return modificationSkill;
			}

		});

	}
}