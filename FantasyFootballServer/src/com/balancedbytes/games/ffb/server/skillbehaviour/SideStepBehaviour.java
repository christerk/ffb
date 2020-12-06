package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.PushbackMode;
import com.balancedbytes.games.ffb.PushbackSquare;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.block.StepPushback;
import com.balancedbytes.games.ffb.server.step.action.block.StepPushback.StepState;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerPushback;
import com.balancedbytes.games.ffb.server.util.UtilServerTimer;
import com.balancedbytes.games.ffb.skill.SideStep;
import com.balancedbytes.games.ffb.util.UtilCards;

public class SideStepBehaviour extends SkillBehaviour<SideStep> {
	public SideStepBehaviour() {
		super();

		registerModifier(new StepModifier<StepPushback, StepPushback.StepState>(2) {

			@Override
			public StepCommandStatus handleCommandHook(StepPushback step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				state.sideStepping.put(useSkillCommand.getPlayerId(), useSkillCommand.isSkillUsed());
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepPushback step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				Skill cancellingSkill = UtilCards.getSkillCancelling(actingPlayer.getPlayer(), skill);
				PlayerState playerState = game.getFieldModel().getPlayerState(state.defender);
				FieldModel fieldModel = game.getFieldModel();

				if (state.sideStepping.getOrDefault(state.defender.getId(), true) && state.freeSquareAroundDefender
						&& UtilCards.hasSkill(game, state.defender, skill)
						&& !(cancellingSkill != null && game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer())
								.isAdjacent(game.getFieldModel().getPlayerCoordinate(state.defender)))
						&& !playerState.isProne() && ((state.oldDefenderState == null) || !state.oldDefenderState.isProne())) {
					if (!state.sideStepping.containsKey(state.defender.getId())) {
						UtilServerDialog.showDialog(step.getGameState(),
								new DialogSkillUseParameter(state.defender.getId(), skill, 0), true);
					} else {
						if (state.sideStepping.get(state.defender.getId())) {
							state.pushbackMode = PushbackMode.SIDE_STEP;
							for (int i = 0; i < state.pushbackSquares.length; i++) {
								if (!state.pushbackSquares[i].isSelected()) {
									fieldModel.remove(state.pushbackSquares[i]);
								}
							}
							state.pushbackSquares = UtilServerPushback.findPushbackSquares(game, state.startingPushbackSquare,
									state.pushbackMode);
							boolean sideStepHomePlayer = game.getTeamHome().hasPlayer(state.defender);
							for (PushbackSquare pushbackSquare : state.pushbackSquares) {
								pushbackSquare.setHomeChoice(sideStepHomePlayer);
							}
							fieldModel.add(state.pushbackSquares);
							if ((sideStepHomePlayer && !game.isHomePlaying()) || (!sideStepHomePlayer && game.isHomePlaying())) {
								game.setWaitingForOpponent(true);
								UtilServerTimer.stopTurnTimer(step.getGameState(), System.currentTimeMillis());
							}
						}
						step.publishParameter(new StepParameter(StepParameterKey.STARTING_PUSHBACK_SQUARE, null));
					}
					return true;
				}
				return false;
			}

		});

	}
}
