package com.fumbbl.ffb.server.skillbehaviour.bb2016;

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
import com.fumbbl.ffb.report.bb2016.ReportTentaclesShadowingRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.action.move.StepTentacles;
import com.fumbbl.ffb.server.step.action.move.StepTentacles.StepState;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.skill.Tentacles;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilPlayer;

@RulesCollection(Rules.BB2016)
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
						if (ReRolledActions.TENTACLES_ESCAPE == step.getReRolledAction()) {
							if ((step.getReRollSource() == null)
									|| !UtilServerReRoll.useReRoll(step, step.getReRollSource(), actingPlayer.getPlayer())) {
								rollTentacles = false;
							}
						}
						if (rollTentacles) {
							int[] rollEscape = step.getGameState().getDiceRoller().rollTentaclesEscape();
							boolean successful = DiceInterpreter.getInstance().isTentaclesEscapeSuccessful(rollEscape,
								game.getDefender().getStrengthWithModifiers(), actingPlayer.getStrength());
							int minimumRoll = DiceInterpreter.getInstance().minimumRollTentaclesEscape(
								game.getDefender().getStrengthWithModifiers(), actingPlayer.getStrength());
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
							step.publishParameter(StepParameter.from(StepParameterKey.COORDINATE_FROM, null));
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
