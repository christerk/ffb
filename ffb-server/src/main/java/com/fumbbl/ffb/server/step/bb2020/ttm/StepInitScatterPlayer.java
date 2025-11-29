package com.fumbbl.ffb.server.step.bb2020.ttm;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.GameOptionInt;
import com.fumbbl.ffb.report.ReportPassDeviate;
import com.fumbbl.ffb.report.mixed.ReportSwoopPlayer;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeCrowdPush;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeKTMCrowd;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeTTMHitPlayer;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.action.ttm.UtilThrowTeamMateSequence;
import com.fumbbl.ffb.server.util.UtilServerCatchScatterThrowIn;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerInjury;

/**
 * Step in ttm sequence to scatter the thrown player.
 * <p>
 * Needs to be initialized with stepParameter THROWN_PLAYER_ID. Needs to be
 * initialized with stepParameter THROWN_PLAYER_STATE. Needs to be initialized
 * with stepParameter THROWN_PLAYER_HAS_BALL. Needs to be initialized with
 * stepParameter THROWN_PLAYER_COORDINATE. Needs to be initialized with
 * stepParameter THROW_SCATTER.
 * <p>
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
@RulesCollection(RulesCollection.Rules.BB2020)
public final class StepInitScatterPlayer extends AbstractStep {

	private String thrownPlayerId;
	private PlayerState thrownPlayerState;
	private FieldCoordinate thrownPlayerCoordinate;
	private boolean thrownPlayerHasBall, throwScatter, deviate, isKickedPlayer, crashLanding;
	private Direction swoopDirection;

	public StepInitScatterPlayer(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.INIT_SCATTER_PLAYER;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					// mandatory
					case KICKED_PLAYER_ID:
					case THROWN_PLAYER_ID:
						thrownPlayerId = (String) parameter.getValue();
						break;
					// mandatory
					case KICKED_PLAYER_HAS_BALL:
					case THROWN_PLAYER_HAS_BALL:
						thrownPlayerHasBall = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
						break;
					// mandatory
					case KICKED_PLAYER_COORDINATE:
					case THROWN_PLAYER_COORDINATE:
						thrownPlayerCoordinate = (FieldCoordinate) parameter.getValue();
						break;
					// mandatory
					case KICKED_PLAYER_STATE:
					case THROWN_PLAYER_STATE:
						thrownPlayerState = (PlayerState) parameter.getValue();
						break;
					// mandatory
					case THROW_SCATTER:
						throwScatter = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
						break;
					case IS_KICKED_PLAYER:
						isKickedPlayer = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
						break;
					case PASS_DEVIATES:
						deviate = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
						break;
					case CRASH_LANDING:
						crashLanding = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
						break;
					case DIRECTION:
						swoopDirection = (Direction) parameter.getValue();
						break;
					default:
						break;
				}
			}
		}
		if (thrownPlayerState == null) {
			throw new StepException("StepParameter " + StepParameterKey.THROWN_PLAYER_STATE + " is not initialized.");
		}
		if (thrownPlayerId == null) {
			throw new StepException("StepParameter " + StepParameterKey.THROWN_PLAYER_ID + " is not initialized.");
		}
		if (thrownPlayerCoordinate == null) {
			throw new StepException("StepParameter " + StepParameterKey.THROWN_PLAYER_COORDINATE + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case IS_KICKED_PLAYER:
					isKickedPlayer = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					return true;
				case DIRECTION:
					swoopDirection = (Direction) parameter.getValue();
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
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		Player<?> thrownPlayer = game.getPlayerById(thrownPlayerId);

		if (thrownPlayerState != null && thrownPlayerState.getBase() == PlayerState.PICKED_UP) {
			thrownPlayerState = thrownPlayerState.changeBase(PlayerState.IN_THE_AIR);
			game.getFieldModel().setPlayerState(thrownPlayer, thrownPlayerState);
		}

		if ((thrownPlayer == null) || (thrownPlayerCoordinate == null)) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}
		FieldCoordinate startCoordinate = thrownPlayerCoordinate;
		if (deviate) {
			game.getFieldModel().setRangeRuler(null);
			game.getFieldModel().clearMoveSquares();
		} else if (throwScatter) {
			game.getFieldModel().setRangeRuler(null);
			game.getFieldModel().clearMoveSquares();
			startCoordinate = game.getPassCoordinate();
		}
		UtilThrowTeamMateSequence.ScatterResult scatterResult;
		if (isKickedPlayer && throwScatter) {
			scatterResult = UtilThrowTeamMateSequence.kickPlayer(this, thrownPlayerCoordinate, startCoordinate);
		} else if (deviate) {
			scatterResult = deviate(game, thrownPlayerCoordinate);
		} else if (swoopDirection != null) {
			scatterResult = swoop(startCoordinate, swoopDirection);
		} else {
			scatterResult = UtilThrowTeamMateSequence.scatterPlayer(this, startCoordinate, throwScatter);
		}
		FieldCoordinate endCoordinate = scatterResult.getLastValidCoordinate();
		// send animation before sending player coordinate and state change (otherwise
		// thrown player will be displayed in landing square first)
		getResult()
			.setAnimation(new Animation(swoopDirection != null ? startCoordinate : thrownPlayerCoordinate, endCoordinate, thrownPlayerId, thrownPlayerHasBall));


		UtilServerGame.syncGameModel(this);
		Player<?> playerLandedUpon = null;
		if (scatterResult.isInBounds()) {
			playerLandedUpon = game.getFieldModel().getPlayer(endCoordinate);

			if (playerLandedUpon != null && playerLandedUpon.getId().equals(thrownPlayerId)) {
				playerLandedUpon = null;
			}

			if (playerLandedUpon != null) {
				publishParameter(new StepParameter(StepParameterKey.DROP_THROWN_PLAYER, true));
				InjuryResult injuryResultHitPlayer = UtilServerInjury.handleInjury(this, new InjuryTypeTTMHitPlayer(), null,
					playerLandedUpon, endCoordinate, null, null, ApothecaryMode.HIT_PLAYER);
				publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultHitPlayer));
				GameOptionBoolean alwaysTurnOver = (GameOptionBoolean) game.getOptions().getOptionWithDefault(GameOptionId.END_TURN_WHEN_HITTING_ANY_PLAYER_WITH_TTM);
				if (alwaysTurnOver.isEnabled() || ((game.isHomePlaying() && game.getTeamHome().hasPlayer(playerLandedUpon))
					|| (!game.isHomePlaying() && game.getTeamAway().hasPlayer(playerLandedUpon)))) {
					publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
				}        // crash landing only happens in empty squares
				crashLanding = false;

				// continue loop in end step
				publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, endCoordinate));
				publishParameter(new StepParameter(StepParameterKey.CRASH_LANDING, crashLanding));
				publishParameter(new StepParameter(StepParameterKey.PLAYER_ENTERING_SQUARE, thrownPlayerId));

			} else if (crashLanding) {
				crashLanding = false;
				publishParameter(new StepParameter(StepParameterKey.DROP_THROWN_PLAYER, true));
				publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, endCoordinate));
				publishParameter(new StepParameter(StepParameterKey.CRASH_LANDING, crashLanding));
				publishParameter(new StepParameter(StepParameterKey.PLAYER_ENTERING_SQUARE, thrownPlayerId));
			} else {
				// put thrown player in target coordinate (ball will be handled in right stuff
				// step), end loop
				game.getFieldModel().setPlayerCoordinate(thrownPlayer, endCoordinate);
				game.getFieldModel().setPlayerState(thrownPlayer, thrownPlayerState);
				game.setDefenderId(null);
				publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null));
				publishParameter(new StepParameter(StepParameterKey.PLAYER_ENTERING_SQUARE, thrownPlayerId));
			}
		} else {
			new TtmToCrowdHandler().handle(game, this, thrownPlayer, endCoordinate,
				thrownPlayerHasBall, isKickedPlayer ? new InjuryTypeKTMCrowd() : new InjuryTypeCrowdPush());
		}
		publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_ID, thrownPlayerId));
		publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_STATE, thrownPlayerState));
		publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_HAS_BALL, thrownPlayerHasBall));
		publishParameter(new StepParameter(StepParameterKey.IS_KICKED_PLAYER, isKickedPlayer));
		if (playerLandedUpon != null) {
			publishParameters(UtilServerInjury.dropPlayer(this, playerLandedUpon, ApothecaryMode.HIT_PLAYER, true));
		}
		game.getFieldModel().setPlayerCoordinate(thrownPlayer, endCoordinate);
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private UtilThrowTeamMateSequence.ScatterResult deviate(Game game, FieldCoordinate throwerCoordinate) {
		int directionRoll = getGameState().getDiceRoller().rollScatterDirection();
		int distanceRoll = getGameState().getDiceRoller().rollScatterDistance();
		Direction direction = DiceInterpreter.getInstance().interpretScatterDirectionRoll(game, directionRoll);
		FieldCoordinate coordinateEnd = UtilServerCatchScatterThrowIn.findScatterCoordinate(throwerCoordinate, direction, distanceRoll);
		FieldCoordinate lastValidCoordinate = coordinateEnd;
		int validDistance = distanceRoll;
		while (!FieldCoordinateBounds.FIELD.isInBounds(lastValidCoordinate) && validDistance > 0) {
			validDistance--;
			lastValidCoordinate = UtilServerCatchScatterThrowIn.findScatterCoordinate(throwerCoordinate, direction, validDistance);
		}
		publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, lastValidCoordinate));

		getResult().addReport(new ReportPassDeviate(coordinateEnd, direction, directionRoll, distanceRoll, true));

		return new UtilThrowTeamMateSequence.ScatterResult(lastValidCoordinate, FieldCoordinateBounds.FIELD.isInBounds(coordinateEnd));
	}

	private UtilThrowTeamMateSequence.ScatterResult swoop(FieldCoordinate throwerCoordinate, Direction direction) {
		GameOptionInt distance = (GameOptionInt) getGameState().getGame().getOptions().getOptionWithDefault(GameOptionId.SWOOP_DISTANCE);

		int distanceRoll = distance.getValue() == 0 ?getGameState().getDiceRoller().rollDice(3) : distance.getValue();
		FieldCoordinate coordinateEnd = UtilServerCatchScatterThrowIn.findScatterCoordinate(throwerCoordinate, direction, distanceRoll);
		FieldCoordinate lastValidCoordinate = coordinateEnd;
		int validDistance = distanceRoll;
		while (!FieldCoordinateBounds.FIELD.isInBounds(lastValidCoordinate) && validDistance > 0) {
			validDistance--;
			lastValidCoordinate = UtilServerCatchScatterThrowIn.findScatterCoordinate(throwerCoordinate, direction, validDistance);
		}
		publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, lastValidCoordinate));

		getResult().addReport(new ReportSwoopPlayer(throwerCoordinate, coordinateEnd, direction, distanceRoll));

		return new UtilThrowTeamMateSequence.ScatterResult(lastValidCoordinate, FieldCoordinateBounds.FIELD.isInBounds(coordinateEnd));

	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, thrownPlayerId);
		IServerJsonOption.THROWN_PLAYER_STATE.addTo(jsonObject, thrownPlayerState);
		IServerJsonOption.THROWN_PLAYER_HAS_BALL.addTo(jsonObject, thrownPlayerHasBall);
		IServerJsonOption.THROWN_PLAYER_COORDINATE.addTo(jsonObject, thrownPlayerCoordinate);
		IServerJsonOption.THROW_SCATTER.addTo(jsonObject, throwScatter);
		IServerJsonOption.IS_KICKED_PLAYER.addTo(jsonObject, isKickedPlayer);
		IServerJsonOption.PASS_DEVIATES.addTo(jsonObject, deviate);
		IServerJsonOption.CRASH_LANDING.addTo(jsonObject, crashLanding);
		IServerJsonOption.SCATTER_DIRECTION.addTo(jsonObject, swoopDirection);
		return jsonObject;
	}

	@Override
	public StepInitScatterPlayer initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		thrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(source, jsonObject);
		thrownPlayerState = IServerJsonOption.THROWN_PLAYER_STATE.getFrom(source, jsonObject);
		thrownPlayerHasBall = IServerJsonOption.THROWN_PLAYER_HAS_BALL.getFrom(source, jsonObject);
		thrownPlayerCoordinate = IServerJsonOption.THROWN_PLAYER_COORDINATE.getFrom(source, jsonObject);
		throwScatter = IServerJsonOption.THROW_SCATTER.getFrom(source, jsonObject);
		isKickedPlayer = IServerJsonOption.IS_KICKED_PLAYER.getFrom(source, jsonObject);
		deviate = IServerJsonOption.PASS_DEVIATES.getFrom(source, jsonObject);
		crashLanding = IServerJsonOption.CRASH_LANDING.getFrom(source, jsonObject);
		swoopDirection = (Direction) IServerJsonOption.SCATTER_DIRECTION.getFrom(source, jsonObject);
		return this;
	}

}
