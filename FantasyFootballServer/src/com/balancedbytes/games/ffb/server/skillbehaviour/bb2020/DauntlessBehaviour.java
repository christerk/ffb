package com.balancedbytes.games.ffb.server.skillbehaviour.bb2020;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.IReport;
import com.balancedbytes.games.ffb.report.ReportDauntlessRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.mechanic.RollMechanic;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.block.StepDauntless;
import com.balancedbytes.games.ffb.server.step.action.block.StepDauntless.StepState;
import com.balancedbytes.games.ffb.server.step.bb2020.multiblock.StepDauntlessMultiple;
import com.balancedbytes.games.ffb.server.util.ServerUtilBlock;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.skill.Dauntless;
import com.balancedbytes.games.ffb.util.UtilCards;

@RulesCollection(Rules.BB2020)
public class DauntlessBehaviour extends SkillBehaviour<Dauntless> {
	public DauntlessBehaviour() {
		super();

		registerModifier(new StepModifier<StepDauntless, StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepDauntless step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepDauntless step, StepState state) {
				boolean doNextStep = true;
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				int defenderStrength = game.getDefender().getStrengthWithModifiers();
				boolean lessStrengthThanDefender = (actingPlayer.getStrength() < defenderStrength);
				boolean usesSpecialBlockingRules = actingPlayer.getPlayer().hasSkillProperty(NamedProperties.makesStrengthTestObsolete);

				if (UtilCards.hasSkill(actingPlayer, skill) && lessStrengthThanDefender
						&& ((state.usingStab == null) || !state.usingStab) && !usesSpecialBlockingRules) {
					boolean doDauntless = true;
					if (ReRolledActions.DAUNTLESS == step.getReRolledAction()) {
						if ((step.getReRollSource() == null)
								|| !UtilServerReRoll.useReRoll(step, step.getReRollSource(), actingPlayer.getPlayer())) {
							doDauntless = false;
						}
					}
					if (doDauntless) {
						int dauntlessRoll = step.getGameState().getDiceRoller().rollDauntless();
						int minimumRoll = DiceInterpreter.getInstance().minimumRollDauntless(actingPlayer.getStrength(),
							defenderStrength);
						boolean successful = (dauntlessRoll >= minimumRoll);
						boolean reRolled = ((step.getReRolledAction() == ReRolledActions.DAUNTLESS)
								&& (step.getReRollSource() != null));
						step.getResult().addReport(new ReportDauntlessRoll(actingPlayer.getPlayerId(), successful, dauntlessRoll,
								minimumRoll, reRolled, defenderStrength));
						if (successful) {
							actingPlayer.markSkillUsed(skill);
							step.publishParameter(new StepParameter(StepParameterKey.SUCCESSFUL_DAUNTLESS, true));
						} else {
							if (UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer.getPlayer(),
									ReRolledActions.DAUNTLESS, minimumRoll, false)) {
								doNextStep = false;
							}
						}
					}
				}
				if (doNextStep) {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				}
				return false;
			}
		});

		registerModifier(new AbstractStepModifierMultipleBlock<StepDauntlessMultiple, StepStateMultipleRolls>() {

			@Override
			protected boolean requiresRoll(Player<?> actingPlayer, Player<?> opponentPlayer) {
				return Math.max(1, actingPlayer.getStrengthWithModifiers() - 2) < opponentPlayer.getStrengthWithModifiers();
			}

			@Override
			protected boolean canBeSkipped(Player<?> actingPlayer) {
				return !actingPlayer.hasSkillProperty(NamedProperties.canRollToMatchOpponentsStrength);
			}

			@Override
			protected int skillRoll(StepDauntlessMultiple step) {
				return step.getGameState().getDiceRoller().rollSkill();
			}

			@Override
			protected int minimumRoll(Game game, Player<?> actingPlayer, Player<?> opponentPlayer) {
				RollMechanic mechanic = (RollMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ROLL.name());
				int attackerStrength = ServerUtilBlock.getAttackerStrength(game, actingPlayer, opponentPlayer, true);
				return DiceInterpreter.getInstance().minimumRollDauntless(attackerStrength, opponentPlayer.getStrength() + mechanic.multiBlockDefenderModifier());
			}

			@Override
			protected IReport report(Game game, String playerId, boolean mayBlock, int actualRoll, int minimumRoll, boolean reRolling, String currentTargetId) {
				int strength = game.getPlayerById(currentTargetId).getStrengthWithModifiers();
				return new ReportDauntlessRoll(playerId, mayBlock, actualRoll, minimumRoll, reRolling, strength, currentTargetId);
			}

			@Override
			protected void unhandledTargetsCallback(StepDauntlessMultiple step, StepStateMultipleRolls state) {

			}

			@Override
			protected void successFulRollCallback(StepDauntlessMultiple step, String successfulId) {
				step.publishParameter(new StepParameter(StepParameterKey.PLAYER_ID_DAUNTLESS_SUCCESS, successfulId));
			}

			@Override
			protected void failedRollEffect(StepDauntlessMultiple step) {}

			@Override
			protected ReRolledAction reRolledAction() {
				return ReRolledActions.DAUNTLESS;
			}
		});

	}
}