package com.balancedbytes.games.ffb.server.skillbehaviour.bb2020;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.report.IReport;
import com.balancedbytes.games.ffb.report.ReportChainsawRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypeChainsaw;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.step.bb2020.multiblock.StepBlockChainsawMultiple;
import com.balancedbytes.games.ffb.server.util.UtilServerInjury;
import com.balancedbytes.games.ffb.skill.bb2020.Chainsaw;
import com.balancedbytes.games.ffb.util.UtilPlayer;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ChainsawBehaviour extends SkillBehaviour<Chainsaw> {
	public ChainsawBehaviour() {
		super();

		registerModifier(new AbstractStepModifierMultipleBlock<StepBlockChainsawMultiple, StepStateMultipleRolls>() {
			@Override
			protected ReRolledAction reRolledAction() {
				return ReRolledActions.CHAINSAW;
			}

			@Override
			protected boolean requiresRoll(Player<?> actingPlayer, Player<?> opponentPlayer) {
				return true;
			}

			@Override
			protected boolean canBeSkipped(Player<?> actingPlayer) {
				return !actingPlayer.hasSkillProperty(NamedProperties.blocksLikeChainsaw);
			}

			@Override
			protected int skillRoll(StepBlockChainsawMultiple step) {
				return step.getGameState().getDiceRoller().rollSkill();
			}

			@Override
			protected int minimumRoll(Game game, Player<?> actingPlayer, Player<?> opponentPlayer) {
				return DiceInterpreter.getInstance().minimumRollChainsaw();
			}

			@Override
			protected IReport report(Game game, String playerId, boolean successful, int actualRoll, int minimumRoll, boolean reRolling, String currentTargetId) {
				return new ReportChainsawRoll(playerId, successful, actualRoll,
					minimumRoll, reRolling, null, currentTargetId);
			}

			@Override
			protected void unhandledTargetsCallback(StepBlockChainsawMultiple step, StepStateMultipleRolls state) {
				Game game = step.getGameState().getGame();
				Player<?> attacker = game.getActingPlayer().getPlayer();

				state.blockTargets.forEach(target -> {
					FieldCoordinate attackerCoordinate = game.getFieldModel().getPlayerCoordinate(attacker);
					InjuryResult injuryResultAttacker = UtilServerInjury.handleInjury(step, new InjuryTypeChainsaw(), null,
						attacker, attackerCoordinate, null, ApothecaryMode.ATTACKER);
					if (injuryResultAttacker.injuryContext().isArmorBroken()) {
						StepParameterSet parameterSet = UtilServerInjury.dropPlayer(step, attacker, ApothecaryMode.ATTACKER);
						parameterSet.remove(StepParameterKey.END_TURN);
						step.publishParameters(parameterSet);
						if (UtilPlayer.hasBall(game, attacker)) {
							step.publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
						}
					}
					step.publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultAttacker));
				});
			}

			@Override
			protected void successFulRollCallback(StepBlockChainsawMultiple step, String successfulId) {
				step.getResult().setSound(SoundId.CHAINSAW);
				Game game = step.getGameState().getGame();
				Player<?> defender = game.getPlayerById(successfulId);
				FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(defender);
				InjuryResult injuryResultDefender = UtilServerInjury.handleInjury(step, new InjuryTypeChainsaw(),
					game.getActingPlayer().getPlayer(), defender, defenderCoordinate, null, ApothecaryMode.DEFENDER);
				if (injuryResultDefender.injuryContext().isArmorBroken()) {
					step.publishParameters(UtilServerInjury.dropPlayer(step, defender, ApothecaryMode.DEFENDER));
				}
				step.publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultDefender));
			}

			@Override
			protected void failedRollEffect(StepBlockChainsawMultiple step) {
				step.getResult().setSound(SoundId.CHAINSAW);
			}
		});
	}
}
