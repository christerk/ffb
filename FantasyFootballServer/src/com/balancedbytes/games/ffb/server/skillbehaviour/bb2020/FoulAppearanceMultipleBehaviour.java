package com.balancedbytes.games.ffb.server.skillbehaviour.bb2020;

import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.BlitzState;
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
import com.balancedbytes.games.ffb.server.step.bb2020.multiblock.StepFoulAppearanceMultiple;
import com.balancedbytes.games.ffb.server.step.bb2020.multiblock.StepFoulAppearanceMultiple.StepState;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.skill.FoulAppearance;
import com.balancedbytes.games.ffb.util.UtilCards;

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

				for (BlockTarget target : state.blockTargets) {
					Player<?> player = game.getPlayerById(target.getPlayerId());
					if (UtilCards.hasSkill(player, skill)
						&& !UtilCards.hasSkillToCancelProperty(actingPlayer.getPlayer(), NamedProperties.forceRollBeforeBeingBlocked)) {
						boolean doRoll = true;
				/*	if (ReRolledActions.FOUL_APPEARANCE == step.getReRolledAction()) {
						if ((step.getReRollSource() == null)
								|| !UtilServerReRoll.useReRoll(step, step.getReRollSource(), actingPlayer.getPlayer())) {
							doRoll = false;
							handleFailure(step, state, game, actingPlayer);
						}
					}*/
						if (doRoll) {
							int foulAppearanceRoll = step.getGameState().getDiceRoller().rollSkill();
							int minimumRoll = DiceInterpreter.getInstance().minimumRollResistingFoulAppearance();
							boolean mayBlock = DiceInterpreter.getInstance().isSkillRollSuccessful(foulAppearanceRoll, minimumRoll);
							boolean reRolled = ((step.getReRolledAction() == ReRolledActions.FOUL_APPEARANCE)
								&& (step.getReRollSource() != null));
							step.getResult().addReport(new ReportFoulAppearanceRoll(actingPlayer.getPlayerId(),
								mayBlock, foulAppearanceRoll, minimumRoll, reRolled, null));
							if (mayBlock) {
								state.blockTargets.remove(target);
							} else {
								boolean teamReRoll = UtilServerReRoll.isTeamReRollAvailable(step.getGameState(), actingPlayer.getPlayer());
								boolean proReRoll = UtilServerReRoll.isProReRollAvailable(actingPlayer.getPlayer(), game);

								if (teamReRoll || proReRoll) {
									//TODO dialog
								} else {
									state.blockTargets.remove(target);
								}
							}
							if (!mayBlock && !reRolled) {
								step.getResult().setSound(SoundId.EW);
							}
						}

					} else {
						state.blockTargets.remove(target);
					}
				}
				return false;
			}

			private void handleFailure(StepFoulAppearanceMultiple step, StepState state, Game game, ActingPlayer actingPlayer) {
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
	}
}