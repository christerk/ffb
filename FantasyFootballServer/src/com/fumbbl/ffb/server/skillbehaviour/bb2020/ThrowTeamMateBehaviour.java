package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.PassModifierFactory;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassMechanic;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.PassContext;
import com.fumbbl.ffb.modifiers.PassModifier;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportThrowTeamMateRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.bb2020.StepThrowTeamMate;
import com.fumbbl.ffb.server.step.bb2020.StepThrowTeamMate.StepState;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.common.ScatterPlayer;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.skill.bb2020.ThrowTeamMate;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Collection;
import java.util.Set;

@RulesCollection(Rules.BB2020)
public class ThrowTeamMateBehaviour extends SkillBehaviour<ThrowTeamMate> {
	public ThrowTeamMateBehaviour() {
		super();

		registerModifier(new StepModifier<StepThrowTeamMate, StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepThrowTeamMate step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepThrowTeamMate step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				actingPlayer.setHasPassed(true);
				game.setConcessionPossible(false);
				game.getTurnData().setPassUsed(true);
				UtilServerDialog.hideDialog(step.getGameState());
				Player<?> thrower = game.getActingPlayer().getPlayer();
				boolean doRoll = true;
				if (ReRolledActions.THROW_TEAM_MATE == step.getReRolledAction()) {
					if ((step.getReRollSource() == null) || !UtilServerReRoll.useReRoll(step, step.getReRollSource(), thrower)) {
						handlePassResult(game, state.passResult, step, state);
						doRoll = false;
					}
				}
				if (doRoll) {
					PassModifierFactory passModifierFactory = game.getFactory(FactoryType.Factory.PASS_MODIFIER);
					FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(thrower);
					PassMechanic mechanic = (PassMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.PASS.name());
					PassingDistance passingDistance = mechanic.findPassingDistance(game, throwerCoordinate,
							game.getPassCoordinate(), true);
					Set<PassModifier> passModifiers = passModifierFactory.findModifiers(new PassContext(game, thrower, passingDistance, true));
					int minimumRoll = DiceInterpreter.getInstance().minimumRollThrowTeamMate(passingDistance,
							passModifiers);
					int roll = step.getGameState().getDiceRoller().rollSkill();
					state.passResult = evaluatePass(thrower.getPassingWithModifiers(), roll, passingDistance, passModifiers);
					boolean reRolled = ((step.getReRolledAction() == ReRolledActions.THROW_TEAM_MATE)
							&& (step.getReRollSource() != null));
					step.getResult().addReport(new ReportThrowTeamMateRoll(thrower.getId(), successful, roll, minimumRoll,
							reRolled, passModifiers.toArray(new PassModifier[0]), passingDistance, state.thrownPlayerId));
					if (state.passResult == PassResult.ACCURATE) {
						handlePassResult(game, state.passResult, step, state);
					} else {
						if (step.getReRolledAction() != ReRolledActions.THROW_TEAM_MATE) {
							step.setReRolledAction(ReRolledActions.THROW_TEAM_MATE);

							ReRollSource unusedPassingReroll = UtilCards.getUnusedRerollSource(actingPlayer, ReRolledActions.PASS);
							if (unusedPassingReroll != null) {
								UtilServerDialog.showDialog(step.getGameState(),
										new DialogSkillUseParameter(thrower.getId(), unusedPassingReroll.getSkill(game), minimumRoll), false);
							} else {
								if (reRolled || !UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer.getPlayer(),
										ReRolledActions.THROW_TEAM_MATE, minimumRoll, false)) {
									handlePassResult(game, state.passResult, step, state);
								}
							}
						} else {
							handlePassResult(game, state.passResult, step, state);
						}
					}
				}

				return false;
			}

			private void handlePassResult(Game game, PassResult passResult, StepThrowTeamMate step, StepState state) {
				Player<?> thrower = game.getActingPlayer().getPlayer();
				FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(thrower);

				Player<?> thrownPlayer = game.getPlayerById(state.thrownPlayerId);
				boolean scattersSingleDirection = thrownPlayer != null
					&& thrownPlayer.hasSkillProperty(NamedProperties.ttmScattersInSingleDirection);
				SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
				((ScatterPlayer) factory.forName(SequenceGenerator.Type.ScatterPlayer.name()))
					.pushSequence(new ScatterPlayer.SequenceParams(step.getGameState(), state.thrownPlayerId,
						state.thrownPlayerState, state.thrownPlayerHasBall, throwerCoordinate, scattersSingleDirection, true, passResult));
				step.getResult().setNextAction(StepAction.NEXT_STEP);
			}

			private PassResult evaluatePass(int passValue, int roll, PassingDistance distance, Collection<PassModifier> modifiers) {
				if (passValue <= 0) {
					return PassResult.FUMBLE;
				}

				int resultAfterModifiers = roll - calculateModifiers(modifiers) - distance.getModifier2020();
				if (roll == 1) {
					return PassResult.FUMBLE;
				} else if (roll == 6 || resultAfterModifiers >= passValue) {
					return PassResult.ACCURATE;
				} else if (resultAfterModifiers <= 1) {
					return PassResult.WILDLY_INACCURATE;
				} else {
					return PassResult.INACCURATE;
				}
			}

			private int calculateModifiers(Collection<PassModifier> pPassModifiers) {
				int modifierTotal = 0;
				for (PassModifier passModifier : pPassModifiers) {
					modifierTotal += passModifier.getModifier();
				}
				return modifierTotal;
			}
		});
	}
}
