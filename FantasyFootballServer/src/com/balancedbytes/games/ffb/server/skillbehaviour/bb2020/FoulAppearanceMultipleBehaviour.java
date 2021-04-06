package com.balancedbytes.games.ffb.server.skillbehaviour.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.BlockTarget;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportFoulAppearanceRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.bb2020.multiblock.StepFoulAppearanceMultiple;
import com.balancedbytes.games.ffb.server.step.bb2020.multiblock.StepFoulAppearanceMultiple.StepState;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.skill.FoulAppearance;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;

import java.util.List;

@RulesCollection(Rules.BB2020)
public class FoulAppearanceMultipleBehaviour extends SkillBehaviour<FoulAppearance> {
	public FoulAppearanceMultipleBehaviour() {
		super();

		registerModifier(new StepModifier<StepFoulAppearanceMultiple, StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepFoulAppearanceMultiple step, StepState state,
			                                           ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepFoulAppearanceMultiple step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();

				if (state.firstRun) {
					state.firstRun = false;
					for (BlockTarget target : state.blockTargets) {
						Player<?> player = game.getPlayerById(target.getPlayerId());
						if (UtilCards.hasSkill(player, skill)
							&& !UtilCards.hasSkillToCancelProperty(actingPlayer.getPlayer(), NamedProperties.forceRollBeforeBeingBlocked)) {
							roll(step, actingPlayer, state.blockTargets, target.getPlayerId(), false);
						}
					}
					decideNextStep(step, game, state);

				} else {
					if (!StringTool.isProvided(state.reRollTarget) || state.reRollSource == null || !UtilServerReRoll.useReRoll(step, state.reRollSource, actingPlayer.getPlayer())) {
						step.getResult().setNextAction(StepAction.NEXT_STEP);
					} else {
						roll(step, actingPlayer, state.blockTargets, state.reRollTarget, true);
						decideNextStep(step, game, state);
					}
				}
				return false;
			}

			private void decideNextStep(StepFoulAppearanceMultiple step, Game game, StepState state) {
				if (state.blockTargets.isEmpty()) {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				} else {
					state.teamReRollAvailable = UtilServerReRoll.isTeamReRollAvailable(step.getGameState(), game.getActingPlayer().getPlayer());
					state.proReRollAvailable = UtilServerReRoll.isProReRollAvailable(game.getActingPlayer().getPlayer(), game);
					if (!state.teamReRollAvailable && !state.proReRollAvailable) {
						if (state.blockTargets.size() == 1) {
							step.publishParameter(new StepParameter(StepParameterKey.PLAYER_ID_TO_REMOVE, state.blockTargets.get(0)));
						} else {
							step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
						}
					} else {
						//TODO send dialog
					}
				}
			}

			private void roll(StepFoulAppearanceMultiple step, ActingPlayer actingPlayer, List<BlockTarget> targets, String currentTargetId, boolean reRolling) {
				int foulAppearanceRoll = step.getGameState().getDiceRoller().rollSkill();
				int minimumRoll = DiceInterpreter.getInstance().minimumRollResistingFoulAppearance();
				boolean mayBlock = DiceInterpreter.getInstance().isSkillRollSuccessful(foulAppearanceRoll, minimumRoll);
				step.getResult().addReport(new ReportFoulAppearanceRoll(actingPlayer.getPlayerId(),
					mayBlock, foulAppearanceRoll, minimumRoll, reRolling, null));
				if (mayBlock) {
					targets.stream().filter(target -> target.getPlayerId().equals(currentTargetId))
						.findFirst().ifPresent(targets::remove);
				} else if (!reRolling) {
					step.getResult().setSound(SoundId.EW);
				}
			}
		});
	}
}