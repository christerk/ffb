package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.BlitzState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
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
import com.fumbbl.ffb.server.step.action.block.StepFoulAppearance;
import com.fumbbl.ffb.server.step.action.block.StepFoulAppearance.StepState;
import com.fumbbl.ffb.server.step.bb2020.multiblock.StepFoulAppearanceMultiple;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.skill.FoulAppearance;
import com.fumbbl.ffb.util.UtilCards;

@RulesCollection(Rules.BB2020)
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
				if (game.getFieldModel().getBlitzState() != null) {
					defender = game.getPlayerById(game.getFieldModel().getBlitzState().getSelectedPlayerId());
				} else {
					defender = game.getDefender();
				}

				if (UtilCards.hasSkill(defender, skill)
						&& !UtilCards.hasSkillToCancelProperty(actingPlayer.getPlayer(), NamedProperties.forceRollBeforeBeingBlocked)) {
					boolean doRoll = true;
					if (ReRolledActions.FOUL_APPEARANCE == step.getReRolledAction()) {
						if ((step.getReRollSource() == null)
								|| !UtilServerReRoll.useReRoll(step, step.getReRollSource(), actingPlayer.getPlayer())) {
							doRoll = false;
							handleFailure(step, state, game, actingPlayer);
						}
					}
					if (doRoll) {
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
				actingPlayer.setHasBlocked(true);
				game.getTurnData().setTurnStarted(true);
				step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
				BlitzState blitzState = game.getFieldModel().getBlitzState();
				if (blitzState != null) {
					blitzState.failed();
					game.getTurnData().setBlitzUsed(true);
				}
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
				state.blockTargets.forEach(target -> step.publishParameter(new StepParameter(StepParameterKey.PLAYER_ID_TO_REMOVE, target)));
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