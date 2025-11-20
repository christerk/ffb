package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.bb2020.ReportTentaclesShadowingRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.bb2020.move.StepTentacles;
import com.fumbbl.ffb.server.step.bb2020.move.StepTentacles.StepState;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.skill.common.Tentacles;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilPlayer;

@RulesCollection(Rules.BB2020)
public class TentaclesBehaviour extends SkillBehaviour<Tentacles> {
	public TentaclesBehaviour() {
		super();

		registerModifier(new StepModifier<StepTentacles, StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepTentacles step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepTentacles step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				UtilServerDialog.hideDialog(step.getGameState());
				if (state.usingTentacles == null) {
					if (actingPlayer.isDodging() || actingPlayer.isJumping() || (actingPlayer.hasBlocked() && state.coordinateFrom != null)) {
						Player<?>[] playerArray = UtilPlayer.findAdjacentOpposingPlayersWithSkill(game, state.coordinateFrom, skill,
								false);
						if (ArrayTool.isProvided(playerArray)) {
							String teamId = game.isHomePlaying() ? game.getTeamAway().getId() : game.getTeamHome().getId();
							String[] descriptionArray = new String[playerArray.length];
							for (int i = 0; i < playerArray.length; i++) {
								int attributeDiff = playerArray[i].getStrengthWithModifiers() - actingPlayer.getStrength();
								StringBuilder description = new StringBuilder();
								if (attributeDiff > 0) {
									description.append("(").append(attributeDiff).append(" ST advantage)");
								}
								if (attributeDiff == 0) {
									description.append("(equal ST)");
								}
								if (attributeDiff < 0) {
									description.append("(").append(Math.abs(attributeDiff)).append(" ST disadvantage)");
								}
								descriptionArray[i] = description.toString();
							}
							Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
							UtilServerDialog.showDialog(step.getGameState(),
									new DialogPlayerChoiceParameter(teamId, PlayerChoiceMode.TENTACLES, playerArray, descriptionArray, 1),
									!actingTeam.getId().equals(teamId));
						} else {
							state.usingTentacles = false;
						}
					} else {
						state.usingTentacles = false;
					}
				}
				if (state.usingTentacles != null) {
					boolean doNextStep = true;
					if (state.usingTentacles && (game.getDefender() != null)) {
						boolean rollTentacles = true;
						if (ReRolledActions.TENTACLES == step.getReRolledAction()) {
							if ((step.getReRollSource() == null)
									|| !UtilServerReRoll.useReRoll(step, step.getReRollSource(), game.getDefender())) {
								rollTentacles = false;
								state.usingTentacles = false;
							}
						}
						if (rollTentacles) {
							int roll = step.getGameState().getDiceRoller().rollSkill();
							int stDifference = game.getDefender().getStrengthWithModifiers() - actingPlayer.getPlayer().getStrengthWithModifiers();
							int minimumRoll = Math.max(6 - stDifference, 2);
							boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);

							boolean reRolled = ((step.getReRolledAction() == ReRolledActions.TENTACLES)
									&& (step.getReRollSource() != null));
							step.getResult().addReport(new ReportTentaclesShadowingRoll(skill, game.getDefenderId(), roll,
									successful, minimumRoll, reRolled));
							if (!successful) {
								if (step.getReRolledAction() != ReRolledActions.TENTACLES) {
									if (UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), game.getDefender(),
											ReRolledActions.TENTACLES, minimumRoll, false)) {
										doNextStep = false;
									} else {
										state.usingTentacles = false;
									}
								} else {
									state.usingTentacles = false;
								}
							}
						}
					}
					if (doNextStep) {
						if (state.usingTentacles) {
							actingPlayer.setDodging(false);
							actingPlayer.setJumping(false);
							actingPlayer.setHeldInPlace(true);
							UtilServerPlayerMove.updateMoveSquares(step.getGameState(), false);
							game.getFieldModel().updatePlayerAndBallPosition(actingPlayer.getPlayer(), state.coordinateFrom);
							step.publishParameter(StepParameter.from(StepParameterKey.COORDINATE_FROM, null));
						}
						if (StringTool.isProvided(game.getLastDefenderId())) {
							game.setDefenderId(game.getLastDefenderId());
							game.setLastDefenderId(null);
						}
						step.getResult().setNextAction(StepAction.NEXT_STEP);
					}
				}
				return false;
			}

		});
	}
}
