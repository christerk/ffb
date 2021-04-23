package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportChainsawRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.InjuryType.InjuryTypeChainsaw;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.bb2020.multiblock.StepBlockChainsawMultiple;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.skill.bb2020.Chainsaw;
import com.fumbbl.ffb.util.UtilPlayer;

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
			protected void cleanUp(StepBlockChainsawMultiple step, StepStateMultipleRolls state) {
				Game game = step.getGameState().getGame();
				state.blockTargets.forEach(target -> {
					Player<?> defender = game.getPlayerById(target);
					PlayerState playerState = game.getFieldModel().getPlayerState(defender);
					game.getFieldModel().setPlayerState(defender, playerState.changeSelectedChainsawTarget(false));
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
				} else {
					PlayerState playerState = game.getFieldModel().getPlayerState(defender);
					game.getFieldModel().setPlayerState(defender, playerState.changeSelectedChainsawTarget(false));
				}
				step.publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultDefender));
				injuryResultDefender.report(step);
			}

			@Override
			protected void failedRollEffect(StepBlockChainsawMultiple step) {
				step.getResult().setSound(SoundId.CHAINSAW);
			}
		});
	}
}
