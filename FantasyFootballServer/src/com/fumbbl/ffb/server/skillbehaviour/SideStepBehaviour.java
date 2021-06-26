package com.fumbbl.ffb.server.skillbehaviour;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PushbackMode;
import com.fumbbl.ffb.PushbackSquare;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.bb2016.StepPushback;
import com.fumbbl.ffb.server.step.bb2016.StepPushback.StepState;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerPushback;
import com.fumbbl.ffb.server.util.UtilServerTimer;
import com.fumbbl.ffb.skill.SideStep;
import com.fumbbl.ffb.util.UtilCards;

@RulesCollection(Rules.COMMON)
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
					&& UtilCards.hasSkill(state.defender, skill) && playerState.hasTacklezones()
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
