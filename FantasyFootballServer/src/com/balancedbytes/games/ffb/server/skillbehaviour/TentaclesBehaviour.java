package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.PlayerChoiceMode;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.dialog.DialogPlayerChoiceParameter;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportTentaclesShadowingRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.move.StepTentacles;
import com.balancedbytes.games.ffb.server.step.action.move.StepTentacles.StepState;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.skill.Tentacles;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

@RulesCollection(Rules.COMMON)
public class TentaclesBehaviour extends SkillBehaviour<Tentacles> {
	public TentaclesBehaviour() {
		super();

		registerModifier(new StepModifier<StepTentacles, StepTentacles.StepState>() {

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
					if (actingPlayer.isDodging() || actingPlayer.isLeaping()) {
						Player[] playerArray = UtilPlayer.findAdjacentOpposingPlayersWithSkill(game, state.coordinateFrom, skill,
								false);
						if (ArrayTool.isProvided(playerArray)) {
							String teamId = game.isHomePlaying() ? game.getTeamAway().getId() : game.getTeamHome().getId();
							String[] descriptionArray = new String[playerArray.length];
							for (int i = 0; i < playerArray.length; i++) {
								int attributeDiff = UtilCards.getPlayerStrength(game, playerArray[i]) - actingPlayer.getStrength();
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
						if (ReRolledActions.TENTACLES_ESCAPE == step.getReRolledAction()) {
							if ((step.getReRollSource() == null)
									|| !UtilServerReRoll.useReRoll(step, step.getReRollSource(), actingPlayer.getPlayer())) {
								rollTentacles = false;
							}
						}
						if (rollTentacles) {
							int[] rollEscape = step.getGameState().getDiceRoller().rollTentaclesEscape();
							boolean successful = DiceInterpreter.getInstance().isTentaclesEscapeSuccessful(rollEscape,
									UtilCards.getPlayerStrength(game, game.getDefender()), actingPlayer.getStrength());
							int minimumRoll = DiceInterpreter.getInstance().minimumRollTentaclesEscape(
									UtilCards.getPlayerStrength(game, game.getDefender()), actingPlayer.getStrength());
							boolean reRolled = ((step.getReRolledAction() == ReRolledActions.TENTACLES_ESCAPE)
									&& (step.getReRollSource() != null));
							step.getResult().addReport(new ReportTentaclesShadowingRoll(skill, game.getDefenderId(), rollEscape,
									successful, minimumRoll, reRolled));
							if (successful) {
								state.usingTentacles = false;
							} else {
								if (step.getReRolledAction() != ReRolledActions.TENTACLES_ESCAPE) {
									if (UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer.getPlayer(),
											ReRolledActions.TENTACLES_ESCAPE, minimumRoll, false)) {
										doNextStep = false;
									}
								}
							}
						}
					}
					if (doNextStep) {
						if (state.usingTentacles) {
							game.getFieldModel().updatePlayerAndBallPosition(actingPlayer.getPlayer(), state.coordinateFrom);
							step.publishParameter(new StepParameter(StepParameterKey.FEEDING_ALLOWED, false));
							step.publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
							step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnSuccess);
						} else {
							step.getResult().setNextAction(StepAction.NEXT_STEP);
						}
					}
				}
				return false;
			}

		});
	}
}