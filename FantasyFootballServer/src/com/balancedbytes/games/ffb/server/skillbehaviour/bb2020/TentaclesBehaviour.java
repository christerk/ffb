package com.balancedbytes.games.ffb.server.skillbehaviour.bb2020;

import com.balancedbytes.games.ffb.PlayerChoiceMode;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.dialog.DialogPlayerChoiceParameter;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportTentaclesShadowingRoll2020;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.action.move.StepTentacles;
import com.balancedbytes.games.ffb.server.step.action.move.StepTentacles.StepState;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerPlayerMove;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.skill.Tentacles;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilPlayer;

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
					if (actingPlayer.isDodging() || actingPlayer.isJumping()) {
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
									description.append("(").append(Math.abs(attributeDiff)).append(" ST disadavantage)");
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
							step.getResult().addReport(new ReportTentaclesShadowingRoll2020(skill, game.getDefenderId(), roll,
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
							actingPlayer.setGoingForIt(true);
							actingPlayer.setDodging(false);
							boolean canSprint = actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canMakeAnExtraGfi);
							actingPlayer.setCurrentMove(actingPlayer.getPlayer().getMovementWithModifiers() + (canSprint ? 3 : 2));
							UtilServerPlayerMove.updateMoveSquares(step.getGameState(), false);
							game.getFieldModel().updatePlayerAndBallPosition(actingPlayer.getPlayer(), state.coordinateFrom);
						}
						step.getResult().setNextAction(StepAction.NEXT_STEP);
					}
				}
				return false;
			}

		});
	}
}
