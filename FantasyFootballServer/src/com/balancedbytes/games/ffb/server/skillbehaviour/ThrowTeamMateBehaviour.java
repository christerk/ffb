package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.factory.PassModifierFactory;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.mechanics.PassMechanic;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.PassContext;
import com.balancedbytes.games.ffb.modifiers.PassModifier;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportThrowTeamMateRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.factory.SequenceGeneratorFactory;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.action.ttm.StepThrowTeamMate;
import com.balancedbytes.games.ffb.server.step.action.ttm.StepThrowTeamMate.StepState;
import com.balancedbytes.games.ffb.server.step.generator.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.generator.common.ScatterPlayer;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.skill.ThrowTeamMate;
import com.balancedbytes.games.ffb.util.UtilCards;

import java.util.Set;

@RulesCollection(Rules.COMMON)
public class ThrowTeamMateBehaviour extends SkillBehaviour<ThrowTeamMate> {
	public ThrowTeamMateBehaviour() {
		super();

		registerModifier(new StepModifier<StepThrowTeamMate, StepThrowTeamMate.StepState>() {

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
						step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
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
					boolean successful = !DiceInterpreter.getInstance().isPassFumble(roll, passingDistance, passModifiers);
					boolean reRolled = ((step.getReRolledAction() == ReRolledActions.THROW_TEAM_MATE)
							&& (step.getReRollSource() != null));
					step.getResult().addReport(new ReportThrowTeamMateRoll(thrower.getId(), successful, roll, minimumRoll,
							reRolled, passModifiers.toArray(new PassModifier[0]), passingDistance, state.thrownPlayerId));
					if (successful) {
						Player<?> thrownPlayer = game.getPlayerById(state.thrownPlayerId);
						boolean scattersSingleDirection = thrownPlayer != null
								&& thrownPlayer.hasSkillProperty(NamedProperties.ttmScattersInSingleDirection);
						SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
						((ScatterPlayer) factory.forName(SequenceGenerator.Type.ScatterPlayer.name()))
							.pushSequence(new ScatterPlayer.SequenceParams(step.getGameState(), state.thrownPlayerId,
								state.thrownPlayerState, state.thrownPlayerHasBall, throwerCoordinate, scattersSingleDirection, true));
						step.getResult().setNextAction(StepAction.NEXT_STEP);
					} else {
						if (step.getReRolledAction() != ReRolledActions.THROW_TEAM_MATE) {
							step.setReRolledAction(ReRolledActions.THROW_TEAM_MATE);

							ReRollSource unusedPassingReroll = UtilCards.getUnusedRerollSource(actingPlayer, ReRolledActions.PASS);
							if (unusedPassingReroll != null) {
								UtilServerDialog.showDialog(step.getGameState(),
										new DialogSkillUseParameter(thrower.getId(), unusedPassingReroll.getSkill(game), minimumRoll), false);
							} else {
								if (!UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer.getPlayer(),
										ReRolledActions.THROW_TEAM_MATE, minimumRoll, false)) {
									step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
								}
							}
						} else {
							step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
						}
					}
				}

				return false;
			}

		});
	}
}
