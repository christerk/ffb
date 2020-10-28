package com.balancedbytes.games.ffb.server.step.action.ttm;

import java.util.List;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.commands.ClientCommandSwoop;
import com.balancedbytes.games.ffb.report.ReportSwoopPlayer;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.step.UtilServerSteps;
import com.balancedbytes.games.ffb.server.step.action.common.ApothecaryMode;
import com.balancedbytes.games.ffb.server.util.UtilServerCatchScatterThrowIn;
import com.balancedbytes.games.ffb.server.util.UtilServerGame;
import com.balancedbytes.games.ffb.server.util.UtilServerInjury;
import com.balancedbytes.games.ffb.server.util.UtilServerPlayerSwoop;
import com.balancedbytes.games.ffb.util.UtilActingPlayer;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in the move sequence to handle skill SWOOP.
 * 
 * Needs to be initialized with stepParameter THROWN_PLAYER_ID. Needs to be
 * initialized with stepParameter THROWN_PLAYER_STATE. Needs to be initialized
 * with stepParameter THROWN_PLAYER_HAS_BALL. Needs to be initialized with
 * stepParameter THROWN_PLAYER_COORDINATE. Needs to be initialized with
 * stepParameter THROW_SCATTER.
 * 
 * Sets stepParameter CATCH_SCATTER_THROW_IN_MODE for all steps on the stack.
 * Sets stepParameter DROP_TTM_PLAYER for all steps on the stack. Sets
 * stepParameter END_TURN for all steps on the stack. Sets stepParameter
 * INJURY_RESULT for all steps on the stack. Sets stepParameter
 * THROWIN_COORDINATE for all steps on the stack. Sets stepParameter
 * THROWN_PLAYER_ID for all steps on the stack. Sets stepParameter
 * THROWN_PLAYER_STATE for all steps on the stack. Sets stepParameter
 * THROWN_PLAYER_HAS_BALL for all steps on the stack. Sets stepParameter
 * THROWN_PLAYER_COORDINATE for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepSwoop extends AbstractStep {

	private String fThrownPlayerId;
	private PlayerState fThrownPlayerState;
	private boolean fThrownPlayerHasBall;
	private FieldCoordinate fThrownPlayerCoordinate;
	private boolean fThrowScatter;
	private FieldCoordinate fCoordinateFrom;
	private FieldCoordinate fCoordinateTo;
	private String fGotoLabelOnFallDown;

	public StepSwoop(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.SWOOP;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
				// mandatory
				case THROWN_PLAYER_ID:
					fThrownPlayerId = (String) parameter.getValue();
					break;
					// mandatory
				case THROWN_PLAYER_HAS_BALL:
					fThrownPlayerHasBall = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					break;
					// mandatory
				case THROWN_PLAYER_COORDINATE:
					fThrownPlayerCoordinate = (FieldCoordinate) parameter.getValue();
					break;
					// mandatory
				case THROWN_PLAYER_STATE:
					fThrownPlayerState = (PlayerState) parameter.getValue();
					break;
					// mandatory
				case THROW_SCATTER:
					fThrowScatter = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					break;
				case GOTO_LABEL_ON_FALL_DOWN:
					fGotoLabelOnFallDown = (String) parameter.getValue();
					break;
				default:
					break;
				}
			}
		}
		if (fThrownPlayerState == null) {
			throw new StepException("StepParameter " + StepParameterKey.THROWN_PLAYER_STATE + " is not initialized.");
		}
		if (fThrownPlayerId == null) {
			throw new StepException("StepParameter " + StepParameterKey.THROWN_PLAYER_ID + " is not initialized.");
		}
		if (fThrownPlayerCoordinate == null) {
			throw new StepException(
					"StepParameter " + StepParameterKey.THROWN_PLAYER_COORDINATE + " is not initialized.");
		}
		if (fGotoLabelOnFallDown == null) {
			throw new StepException(
					"StepParameter " + StepParameterKey.GOTO_LABEL_ON_FALL_DOWN + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
			case COORDINATE_FROM:
				fCoordinateFrom = (FieldCoordinate) pParameter.getValue();
				return true;
			case COORDINATE_TO:
				fCoordinateTo = (FieldCoordinate) pParameter.getValue();
				return true;
			default:
				break;
			}
		}
		return false;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
			case CLIENT_SWOOP:
				ClientCommandSwoop swoopCommand = (ClientCommandSwoop) pReceivedCommand.getCommand();
				fCoordinateTo = swoopCommand.getTargetCoordinate();
				if (!UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), pReceivedCommand)) {
					fCoordinateTo = fCoordinateTo.transform();
				}

				executeSwoop();
				break;
			default:
				break;
			}
		} else if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeSwoop() {
		GameState gameState = getGameState();
		Game game = gameState.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Player swoopingPlayer = actingPlayer.getPlayer();

		if (UtilCards.hasSkill(game, swoopingPlayer, ServerSkill.SWOOP)) {
			// Send animation moving the player to the initial target square

			fCoordinateFrom = game.getFieldModel().getPlayerCoordinate(swoopingPlayer);

			Direction playerScatter = null;
			int scatterRoll = getGameState().getDiceRoller().rollThrowInDirection();
			if (fCoordinateFrom.getX() < fCoordinateTo.getX()) {
				playerScatter = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.EAST, scatterRoll);
			} else if (fCoordinateFrom.getX() > fCoordinateTo.getX()) {
				playerScatter = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.WEST, scatterRoll);
			} else if (fCoordinateFrom.getY() < fCoordinateTo.getY()) {
				playerScatter = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.SOUTH, scatterRoll);
			} else { // coordinateFrom.getY() > coordinateTo.getY()
				playerScatter = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.NORTH, scatterRoll);
			}
			fCoordinateTo = UtilServerCatchScatterThrowIn.findScatterCoordinate(fCoordinateFrom, playerScatter, 1);
			getResult().addReport(new ReportSwoopPlayer(fCoordinateFrom, fCoordinateTo,
					new Direction[] { playerScatter }, new int[] { scatterRoll }));
			if (!FieldCoordinateBounds.FIELD.isInBounds(fCoordinateTo)) {
				// Out of bounds
				game.getFieldModel().setPlayerState(swoopingPlayer, new PlayerState(PlayerState.FALLING));
				InjuryResult injuryResultThrownPlayer = UtilServerInjury.handleInjury(this, InjuryType.CROWDPUSH, null, swoopingPlayer, fCoordinateFrom, null, ApothecaryMode.THROWN_PLAYER);
				publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultThrownPlayer));
				if (fThrownPlayerHasBall) {
					publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.THROW_IN));
					publishParameter(new StepParameter(StepParameterKey.THROW_IN_COORDINATE, fCoordinateFrom));
					publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
				}
				// end loop
				publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null));
				getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFallDown);
			} else {
				// Move player
				game.getFieldModel().setPlayerCoordinate(swoopingPlayer, fCoordinateTo);
		        getResult().setSound(SoundId.SWOOP);
				if (fThrownPlayerHasBall) {
					game.getFieldModel().setBallCoordinate(fCoordinateTo);
				}
				int currentMove = actingPlayer.getCurrentMove() + 1;
				actingPlayer.setCurrentMove(currentMove);
				if (currentMove < actingPlayer.getPlayer().getMovement()) {
					// Still swooping
					UtilServerPlayerSwoop.updateSwoopSquares(gameState, swoopingPlayer);
				} else {
					// Landing
					List<Player> playersInSquare = game.getFieldModel().getPlayers(fCoordinateTo);
					boolean crashed = false;
					for(Player p : playersInSquare) {
						if (p != swoopingPlayer) {
							// Landed on another player
							publishParameter(new StepParameter(StepParameterKey.DROP_THROWN_PLAYER, true));
							InjuryResult injuryResultHitPlayer = UtilServerInjury.handleInjury(this, InjuryType.TTM_HIT_PLAYER, null, p, fCoordinateTo, null, ApothecaryMode.HIT_PLAYER);
							publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultHitPlayer));
							if ((game.isHomePlaying() && game.getTeamHome().hasPlayer(p)) || (!game.isHomePlaying() && game.getTeamAway().hasPlayer(p))) {
								publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
							}

							if (fThrownPlayerHasBall) {
								// Hide the ball from the play while scatters are dealt with.
								game.getFieldModel().setBallCoordinate(null);
							}
							
							publishParameters(UtilServerInjury.dropPlayer(this, p, ApothecaryMode.HIT_PLAYER));

						    publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_ID, fThrownPlayerId));
						    publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_STATE, fThrownPlayerState));
						    publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_HAS_BALL, fThrownPlayerHasBall));
							
							publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, fCoordinateTo));
							crashed = true;
							break; // Stop looking for more players to crash on
						}
					}
					if (crashed) {
						getResult().setNextAction(StepAction.NEXT_STEP);
						//getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFallDown);
					} else {
						getResult().setNextAction(StepAction.NEXT_STEP);
					}
				}
			}
			//publishParameter(new StepParameter(StepParameterKey.COORDINATE_TO, fCoordinateTo));
		}		

	}

	private void executeStep() {
		GameState gameState = getGameState();
		Game game = gameState.getGame();

		Player thrownPlayer = game.getPlayerById(fThrownPlayerId);
		if ((thrownPlayer == null) || (fThrownPlayerCoordinate == null))  {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}
		FieldCoordinate passCoordinate = fThrownPlayerCoordinate;
		if (fThrowScatter) {
			game.getFieldModel().setRangeRuler(null);
			game.getFieldModel().clearMoveSquares();
			passCoordinate = game.getPassCoordinate();

			// Render flying animation
			getResult().setAnimation(new Animation(fThrownPlayerCoordinate, passCoordinate, fThrownPlayerId, fThrownPlayerHasBall));
			UtilServerGame.syncGameModel(this);

			// Move player
			game.getFieldModel().setPlayerCoordinate(thrownPlayer, passCoordinate);
			UtilActingPlayer.changeActingPlayer(game, fThrownPlayerId, PlayerAction.SWOOP, false);
			if (fThrownPlayerHasBall) {
				game.getFieldModel().setBallCoordinate(passCoordinate);
			}
			game.getActingPlayer().setCurrentMove(thrownPlayer.getMovement()-3);

			publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_ID, fThrownPlayerId));
			publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_STATE, fThrownPlayerState));
			publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_HAS_BALL, fThrownPlayerHasBall));

			UtilServerGame.syncGameModel(this);
		}

		if (fCoordinateTo == null) {
			UtilServerPlayerSwoop.updateSwoopSquares(gameState, thrownPlayer);
		}
		//getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, fThrownPlayerId);
		IServerJsonOption.THROWN_PLAYER_STATE.addTo(jsonObject, fThrownPlayerState);
		IServerJsonOption.THROWN_PLAYER_HAS_BALL.addTo(jsonObject, fThrownPlayerHasBall);
		IServerJsonOption.THROWN_PLAYER_COORDINATE.addTo(jsonObject, fThrownPlayerCoordinate);
		IServerJsonOption.THROW_SCATTER.addTo(jsonObject, fThrowScatter);
		IServerJsonOption.COORDINATE_FROM.addTo(jsonObject, fCoordinateFrom);
		IServerJsonOption.COORDINATE_TO.addTo(jsonObject, fCoordinateTo);
		return jsonObject;
	}

	@Override
	public StepSwoop initFrom(JsonValue pJsonValue) {
		super.initFrom(pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fThrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(jsonObject);
		fThrownPlayerState = IServerJsonOption.THROWN_PLAYER_STATE.getFrom(jsonObject);
		fThrownPlayerHasBall = IServerJsonOption.THROWN_PLAYER_HAS_BALL.getFrom(jsonObject);
		fThrownPlayerCoordinate = IServerJsonOption.THROWN_PLAYER_COORDINATE.getFrom(jsonObject);
		fThrowScatter = IServerJsonOption.THROW_SCATTER.getFrom(jsonObject);
		fCoordinateFrom = IServerJsonOption.COORDINATE_FROM.getFrom(jsonObject);
		fCoordinateTo = IServerJsonOption.COORDINATE_TO.getFrom(jsonObject);
		return this;
	}

}
