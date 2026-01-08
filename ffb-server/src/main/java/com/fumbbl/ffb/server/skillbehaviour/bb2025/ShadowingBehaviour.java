package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.mixed.ReportTentaclesShadowingRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.bb2025.move.StepShadowing;
import com.fumbbl.ffb.server.step.bb2025.move.StepShadowing.StepState;
import com.fumbbl.ffb.server.util.ServerUtilBlock;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.skill.bb2025.Shadowing;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;

@RulesCollection(Rules.BB2025)
public class ShadowingBehaviour extends SkillBehaviour<Shadowing> {
	public ShadowingBehaviour() {
		super();

		registerModifier(new StepModifier<StepShadowing, StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepShadowing step, StepState state,
				ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepShadowing step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				UtilServerDialog.hideDialog(step.getGameState());
				boolean doNextStep = true;
				boolean doShadowing = (!state.usingDivingTackle && (game.getTurnMode() != TurnMode.KICKOFF_RETURN));
				if (doShadowing && (state.coordinateFrom != null) && (state.usingShadowing == null)) {
					Player<?>[] shadowers =
						UtilPlayer.findAdjacentOpposingPlayersWithSkill(game, state.coordinateFrom, skill, true);
					shadowers = UtilPlayer.filterThrower(game, shadowers);
					if (game.getTurnMode() == TurnMode.DUMP_OFF) {
						shadowers = UtilPlayer.filterAttackerAndDefender(game, shadowers);
					}

					shadowers = Arrays.stream(shadowers).filter(
							shadower -> shadower.getMovementWithModifiers() > step.getGameState().shadowingCount(shadower.getId()))
						.toArray(Player[]::new);

					if (ArrayTool.isProvided(shadowers)) {
						String teamId = game.isHomePlaying() ? game.getTeamAway().getId() : game.getTeamHome().getId();
						Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
						UtilServerDialog.showDialog(step.getGameState(),
							new DialogPlayerChoiceParameter(teamId, PlayerChoiceMode.SHADOWING, shadowers, null, 1),
							!actingTeam.getId().equals(teamId));
						doNextStep = false;
					} else {
						state.usingShadowing = false;
					}
				}
				if (doShadowing && (state.coordinateFrom != null) && (state.usingShadowing != null)) {
					doNextStep = true;
					if (state.usingShadowing && (game.getDefender() != null)) {
						boolean rollShadowing = true;
						if (ReRolledActions.SHADOWING == step.getReRolledAction()) {
							if ((step.getReRollSource() == null)
								|| !UtilServerReRoll.useReRoll(step, step.getReRollSource(), game.getDefender())) {
								rollShadowing = false;
								state.usingShadowing = false;
							}
						}
						if (rollShadowing) {
							step.getGameState().addShadower(game.getDefenderId());
							int roll = step.getGameState().getDiceRoller().rollSkill();
							int minimumRoll = 4;
							boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
							boolean reRolled = ((step.getReRolledAction() == ReRolledActions.SHADOWING)
								&& (step.getReRollSource() != null));
							step.getResult().addReport(new ReportTentaclesShadowingRoll(skill, game.getDefenderId(), roll,
								successful, minimumRoll, reRolled));
							if (!successful) {
								if (step.getReRolledAction() != ReRolledActions.SHADOWING) {
									if (UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), game.getDefender(),
										ReRolledActions.SHADOWING, minimumRoll, false)) {
										doNextStep = false;
									} else {
										state.usingShadowing = false;
									}
								} else {
									state.usingShadowing = false;
								}
							}
						}
					}
					if (doNextStep && state.usingShadowing) {
						game.getFieldModel().updatePlayerAndBallPosition(game.getDefender(), state.coordinateFrom);
						if (state.shadowerWasPreviousDefender) {
							state.defenderPosition = state.coordinateFrom;
						}
						UtilServerPlayerMove.updateMoveSquares(step.getGameState(), actingPlayer.isJumping());
						ServerUtilBlock.updateDiceDecorations(step.getGameState());
						step.publishParameter(
							StepParameter.from(StepParameterKey.PLAYER_ENTERING_SQUARE, game.getDefender().getId()));
					}
				}
				if (doNextStep) {
					if (state.defenderPosition != null) {
						Player<?> defender = game.getFieldModel().getPlayer(state.defenderPosition);
						game.setDefenderId((defender != null) ? defender.getId() : null);
					}
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				}
				return false;
			}

		});
	}
}
