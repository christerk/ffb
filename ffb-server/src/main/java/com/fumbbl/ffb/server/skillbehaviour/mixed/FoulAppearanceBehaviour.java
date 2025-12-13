package com.fumbbl.ffb.server.skillbehaviour.mixed;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportFoulAppearanceRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.mixed.StepFoulAppearance;
import com.fumbbl.ffb.server.step.mixed.StepFoulAppearance.StepState;
import com.fumbbl.ffb.server.step.bb2020.StepStateMultipleRolls;
import com.fumbbl.ffb.server.step.mixed.multiblock.StepFoulAppearanceMultiple;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.skill.common.FoulAppearance;
import com.fumbbl.ffb.util.UtilCards;

import java.util.ArrayList;

@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class FoulAppearanceBehaviour extends SkillBehaviour<FoulAppearance> {
	public FoulAppearanceBehaviour() {
		super();

		registerModifier(new StepModifier<StepFoulAppearance, StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepFoulAppearance step, StepState state,
																								 ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepFoulAppearance step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				Player<?> defender;
				if (game.getFieldModel().getTargetSelectionState() != null) {
					defender = game.getPlayerById(game.getFieldModel().getTargetSelectionState().getSelectedPlayerId());
				} else {
					defender = game.getDefender();
				}

				if (UtilCards.hasSkill(defender, skill)
					&& !UtilCards.hasSkillToCancelProperty(actingPlayer.getPlayer(), NamedProperties.forceRollBeforeBeingBlocked)
				) {
					boolean doRoll = true;
					if (ReRolledActions.FOUL_APPEARANCE == step.getReRolledAction()) {
						if ((step.getReRollSource() == null)
							|| !UtilServerReRoll.useReRoll(step, step.getReRollSource(), actingPlayer.getPlayer())) {
							doRoll = false;
							handleFailure(step, state, game, actingPlayer);
						}
					}
					if (doRoll) {
						step.commitTargetSelection();
						int foulAppearanceRoll = step.getGameState().getDiceRoller().rollSkill();
						int minimumRoll = DiceInterpreter.getInstance().minimumRollResistingFoulAppearance();
						boolean mayBlock = DiceInterpreter.getInstance().isSkillRollSuccessful(foulAppearanceRoll, minimumRoll);
						boolean reRolled = ((step.getReRolledAction() == ReRolledActions.FOUL_APPEARANCE)
							&& (step.getReRollSource() != null));
						step.getResult().addReport(new ReportFoulAppearanceRoll(actingPlayer.getPlayerId(),
							mayBlock, foulAppearanceRoll, minimumRoll, reRolled, null));
						if (mayBlock) {
							step.getResult().setNextAction(StepAction.NEXT_STEP);
						} else {
							if (reRolled || !UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer.getPlayer(),
								ReRolledActions.FOUL_APPEARANCE, minimumRoll, false)) {
								handleFailure(step, state, game, actingPlayer);
							}
						}
						if (!mayBlock && !reRolled) {
							step.getResult().setSound(SoundId.EW);
						}
					}
				} else {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				}
				return false;
			}

			private void handleFailure(StepFoulAppearance step, StepState state, Game game, ActingPlayer actingPlayer) {
				PlayerAction playerAction = actingPlayer.getPlayerAction();
				if (actingPlayer.isStandingUp() &&
					(playerAction == PlayerAction.BLITZ_MOVE
						|| playerAction != null && playerAction.isBlockAction()
						|| playerAction == PlayerAction.GAZE_MOVE
						|| playerAction != null && playerAction.isKickingDowned())) {
					Player<?> player = actingPlayer.getPlayer();
					PlayerState playerState = game.getFieldModel().getPlayerState(player);
					game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.PRONE).changeActive(false));
				}
				actingPlayer.setHasBlocked(true);
				game.getTurnData().setTurnStarted(true);
				step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
				TargetSelectionState targetSelectionState = game.getFieldModel().getTargetSelectionState();
				if (targetSelectionState != null) {
					targetSelectionState.failed();
					if (playerAction != null && playerAction.isBlitzing()) {
						game.getTurnData().setBlitzUsed(true);
					}
				}

				if (playerAction == PlayerAction.GAZE || (playerAction != null && playerAction.isBlockAction())) {
					step.publishParameter(StepParameter.from(StepParameterKey.END_PLAYER_ACTION, true));
				}
				game.setDefenderId(null);
			}
		});

		registerModifier(new AbstractStepModifierMultipleBlock<StepFoulAppearanceMultiple, StepStateMultipleRolls>() {

			@Override
			protected boolean requiresRoll(Player<?> actingPlayer, Player<?> opponentPlayer) {
				return UtilCards.hasSkillWithProperty(opponentPlayer, NamedProperties.forceRollBeforeBeingBlocked);
			}

			@Override
			protected boolean canBeSkipped(Player<?> actingPlayer) {
				return UtilCards.hasSkillToCancelProperty(actingPlayer, NamedProperties.forceRollBeforeBeingBlocked);
			}

			@Override
			protected int skillRoll(StepFoulAppearanceMultiple step) {
				return step.getGameState().getDiceRoller().rollSkill();
			}

			@Override
			protected int minimumRoll(Game game, Player<?> actingPlayer, Player<?> opponentPlayer) {
				return DiceInterpreter.getInstance().minimumRollResistingFoulAppearance();
			}

			@Override
			protected IReport report(Game game, String playerId, boolean mayBlock, int actualRoll, int minimumRoll, boolean reRolling, String currentTargetId) {
				return new ReportFoulAppearanceRoll(playerId, mayBlock, actualRoll, minimumRoll, reRolling, null, currentTargetId);
			}

			@Override
			protected void unhandledTargetsCallback(StepFoulAppearanceMultiple step, StepStateMultipleRolls state) {
				for (String target : new ArrayList<>(state.blockTargets)) {
					Game game = step.getGameState().getGame();
					Player<?> player = game.getPlayerById(target);
					PlayerState playerState = game.getFieldModel().getPlayerState(player);
					game.getFieldModel().setPlayerState(player, playerState.changeSelectedStabTarget(false).changeSelectedBlockTarget(false));
					step.publishParameter(new StepParameter(StepParameterKey.PLAYER_ID_TO_REMOVE, target));
				}
			}

			@Override
			protected void cleanUp(StepFoulAppearanceMultiple step, StepStateMultipleRolls state) {

			}

			@Override
			protected void successFulRollCallback(StepFoulAppearanceMultiple step, String successfulId) {

			}

			@Override
			protected void failedRollEffect(StepFoulAppearanceMultiple step) {
				step.getResult().setSound(SoundId.EW);
			}

			@Override
			protected ReRolledAction reRolledAction() {
				return ReRolledActions.FOUL_APPEARANCE;
			}
		});
	}
}