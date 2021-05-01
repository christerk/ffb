package com.fumbbl.ffb.server.skillbehaviour;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportSwoopPlayer;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.InjuryType.InjuryTypeCrowdPush;
import com.fumbbl.ffb.server.InjuryType.InjuryTypeTTMHitPlayer;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.action.ttm.StepSwoop;
import com.fumbbl.ffb.server.step.action.ttm.StepSwoop.StepState;
import com.fumbbl.ffb.server.util.UtilServerCatchScatterThrowIn;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.server.util.UtilServerPlayerSwoop;
import com.fumbbl.ffb.skill.Swoop;

import java.util.List;

@RulesCollection(Rules.COMMON)
public class SwoopBehaviour extends SkillBehaviour<Swoop> {
	public SwoopBehaviour() {
		super();

		registerModifier(new StepModifier<StepSwoop, StepSwoop.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepSwoop step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepSwoop step, StepState state) {
				GameState gameState = step.getGameState();
				Game game = gameState.getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				Player<?> swoopingPlayer = actingPlayer.getPlayer();

				if (swoopingPlayer.hasSkillProperty(NamedProperties.ttmScattersInSingleDirection)) {
					// Send animation moving the player to the initial target square

					state.coordinateFrom = game.getFieldModel().getPlayerCoordinate(swoopingPlayer);

					Direction playerScatter;
					int scatterRoll = step.getGameState().getDiceRoller().rollThrowInDirection();
					if (state.coordinateFrom.getX() < state.coordinateTo.getX()) {
						playerScatter = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.EAST, scatterRoll);
					} else if (state.coordinateFrom.getX() > state.coordinateTo.getX()) {
						playerScatter = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.WEST, scatterRoll);
					} else if (state.coordinateFrom.getY() < state.coordinateTo.getY()) {
						playerScatter = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.SOUTH, scatterRoll);
					} else { // coordinateFrom.getY() > coordinateTo.getY()
						playerScatter = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.NORTH, scatterRoll);
					}
					state.coordinateTo = UtilServerCatchScatterThrowIn.findScatterCoordinate(state.coordinateFrom, playerScatter,
							1);
					step.getResult().addReport(new ReportSwoopPlayer(state.coordinateFrom, state.coordinateTo,
							new Direction[] { playerScatter }, new int[] { scatterRoll }));
					if (!FieldCoordinateBounds.FIELD.isInBounds(state.coordinateTo)) {
						// Out of bounds
						game.getFieldModel().setPlayerState(swoopingPlayer, new PlayerState(PlayerState.FALLING));
						InjuryResult injuryResultThrownPlayer = UtilServerInjury.handleInjury(step, new InjuryTypeCrowdPush(), null,
								swoopingPlayer, state.coordinateFrom, null, null, ApothecaryMode.THROWN_PLAYER);
						step.publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultThrownPlayer));
						if (state.thrownPlayerHasBall) {
							step.publishParameter(
									new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.THROW_IN));
							step.publishParameter(new StepParameter(StepParameterKey.THROW_IN_COORDINATE, state.coordinateFrom));
							step.publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
						}
						// end loop
						step.publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null));
						step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFallDown);
					} else {
						// Move player
						game.getFieldModel().setPlayerCoordinate(swoopingPlayer, state.coordinateTo);
						step.getResult().setSound(SoundId.SWOOP);
						if (state.thrownPlayerHasBall) {
							game.getFieldModel().setBallCoordinate(state.coordinateTo);
						}
						int currentMove = actingPlayer.getCurrentMove() + 1;
						actingPlayer.setCurrentMove(currentMove);
						if (currentMove < actingPlayer.getPlayer().getMovementWithModifiers()) {
							// Still swooping
							UtilServerPlayerSwoop.updateSwoopSquares(gameState, swoopingPlayer);
						} else {
							// Landing
							List<Player<?>> playersInSquare = game.getFieldModel().getPlayers(state.coordinateTo);
							boolean crashed = false;
							for (Player<?> p : playersInSquare) {
								if (p != swoopingPlayer) {
									// Landed on another player
									step.publishParameter(new StepParameter(StepParameterKey.DROP_THROWN_PLAYER, true));
									InjuryResult injuryResultHitPlayer = UtilServerInjury.handleInjury(step, new InjuryTypeTTMHitPlayer(),
											null, p, state.coordinateTo, null, null, ApothecaryMode.HIT_PLAYER);
									step.publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultHitPlayer));
									if ((game.isHomePlaying() && game.getTeamHome().hasPlayer(p))
											|| (!game.isHomePlaying() && game.getTeamAway().hasPlayer(p))) {
										step.publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
									}

									if (state.thrownPlayerHasBall) {
										// Hide the ball from the play while scatters are dealt with.
										game.getFieldModel().setBallCoordinate(null);
									}

									step.publishParameters(UtilServerInjury.dropPlayer(step, p, ApothecaryMode.HIT_PLAYER, true));

									step.publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_ID, state.thrownPlayerId));
									step.publishParameter(
											new StepParameter(StepParameterKey.THROWN_PLAYER_STATE, state.thrownPlayerState));
									step.publishParameter(
											new StepParameter(StepParameterKey.THROWN_PLAYER_HAS_BALL, state.thrownPlayerHasBall));

									step.publishParameter(
											new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, state.coordinateTo));
									crashed = true;
									break; // Stop looking for more players to crash on
								}
							}
							if (crashed) {
								step.getResult().setNextAction(StepAction.NEXT_STEP);
								// getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFallDown);
							} else {
								step.getResult().setNextAction(StepAction.NEXT_STEP);
							}
						}
					}
					// publishParameter(new StepParameter(StepParameterKey.COORDINATE_TO,
					// fCoordinateTo));
				}

				return false;
			}

		});
	}
}
