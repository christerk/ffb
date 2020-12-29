package com.balancedbytes.games.ffb.server.step.action.ttm;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypeCrowdPush;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypeKTMCrowd;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypeTTMHitPlayer;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilServerGame;
import com.balancedbytes.games.ffb.server.util.UtilServerInjury;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in ttm sequence to scatter the thrown player.
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
public final class StepInitScatterPlayer extends AbstractStep {

	private String fThrownPlayerId;
	private PlayerState fThrownPlayerState;
	private boolean fThrownPlayerHasBall;
	private FieldCoordinate fThrownPlayerCoordinate;
	private boolean fThrowScatter;
	private boolean fIsKickedPlayer;

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
					fThrownPlayerId = (String) parameter.getValue();
					break;
				// mandatory
				case KICKED_PLAYER_HAS_BALL:
				case THROWN_PLAYER_HAS_BALL:
					fThrownPlayerHasBall = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					break;
				// mandatory
				case KICKED_PLAYER_COORDINATE:
				case THROWN_PLAYER_COORDINATE:
					fThrownPlayerCoordinate = (FieldCoordinate) parameter.getValue();
					break;
				// mandatory
				case KICKED_PLAYER_STATE:
				case THROWN_PLAYER_STATE:
					fThrownPlayerState = (PlayerState) parameter.getValue();
					break;
				// mandatory
				case THROW_SCATTER:
					fThrowScatter = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					break;
				case IS_KICKED_PLAYER:
					fIsKickedPlayer = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
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
			throw new StepException("StepParameter " + StepParameterKey.THROWN_PLAYER_COORDINATE + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
			case IS_KICKED_PLAYER:
				fIsKickedPlayer = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
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
		Player thrownPlayer = game.getPlayerById(fThrownPlayerId);
		if ((thrownPlayer == null) || (fThrownPlayerCoordinate == null)) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}
		FieldCoordinate startCoordinate = fThrownPlayerCoordinate;
		if (fThrowScatter) {
			game.getFieldModel().setRangeRuler(null);
			game.getFieldModel().clearMoveSquares();
			startCoordinate = game.getPassCoordinate();
		}
		UtilThrowTeamMateSequence.ScatterResult scatterResult;
		if (fIsKickedPlayer && fThrowScatter) {
			scatterResult = UtilThrowTeamMateSequence.kickPlayer(this, fThrownPlayerCoordinate, startCoordinate);
		} else {
			scatterResult = UtilThrowTeamMateSequence.scatterPlayer(this, startCoordinate, fThrowScatter);
		}
		FieldCoordinate endCoordinate = scatterResult.getLastValidCoordinate();
		// send animation before sending player coordinate and state change (otherwise
		// thrown player will be displayed in landing square first)
		getResult()
				.setAnimation(new Animation(fThrownPlayerCoordinate, endCoordinate, fThrownPlayerId, fThrownPlayerHasBall));
		UtilServerGame.syncGameModel(this);
		Player playerLandedUpon = null;
		if (scatterResult.isInBounds()) {
			playerLandedUpon = game.getFieldModel().getPlayer(endCoordinate);
			if (playerLandedUpon != null) {
				publishParameter(new StepParameter(StepParameterKey.DROP_THROWN_PLAYER, true));
				InjuryResult injuryResultHitPlayer = UtilServerInjury.handleInjury(this, new InjuryTypeTTMHitPlayer(), null,
						playerLandedUpon, endCoordinate, null, ApothecaryMode.HIT_PLAYER);
				publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultHitPlayer));
				if ((game.isHomePlaying() && game.getTeamHome().hasPlayer(playerLandedUpon))
						|| (!game.isHomePlaying() && game.getTeamAway().hasPlayer(playerLandedUpon))) {
					publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
				}
				// continue loop in end step
				publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, endCoordinate));
			} else {
				// put thrown player in target coordinate (ball we be handled in right stuff
				// step), end loop
				game.getFieldModel().setPlayerCoordinate(thrownPlayer, endCoordinate);
				game.getFieldModel().setPlayerState(thrownPlayer, fThrownPlayerState);
				game.setDefenderId(null);
				publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null));
			}
		} else {
			// throw player out of bounds
			game.getFieldModel().setPlayerState(thrownPlayer, new PlayerState(PlayerState.FALLING));
			if (fIsKickedPlayer) {
				InjuryResult injuryResultKickedPlayer = UtilServerInjury.handleInjury(this, new InjuryTypeKTMCrowd(), null,
						thrownPlayer, endCoordinate, null, ApothecaryMode.THROWN_PLAYER);
				publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultKickedPlayer));
			} else {
				InjuryResult injuryResultThrownPlayer = UtilServerInjury.handleInjury(this, new InjuryTypeCrowdPush(), null,
						thrownPlayer, endCoordinate, null, ApothecaryMode.THROWN_PLAYER);
				publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultThrownPlayer));
			}
			if (fThrownPlayerHasBall) {
				publishParameter(
						new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.THROW_IN));
				publishParameter(new StepParameter(StepParameterKey.THROW_IN_COORDINATE, endCoordinate));
				publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
			}
			// end loop
			publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null));
		}
		publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_ID, fThrownPlayerId));
		publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_STATE, fThrownPlayerState));
		publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_HAS_BALL, fThrownPlayerHasBall));
		if (playerLandedUpon != null) {
			publishParameters(UtilServerInjury.dropPlayer(this, playerLandedUpon, ApothecaryMode.HIT_PLAYER));
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
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
		return jsonObject;
	}

	@Override
	public StepInitScatterPlayer initFrom(Game game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fThrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(game, jsonObject);
		fThrownPlayerState = IServerJsonOption.THROWN_PLAYER_STATE.getFrom(game, jsonObject);
		fThrownPlayerHasBall = IServerJsonOption.THROWN_PLAYER_HAS_BALL.getFrom(game, jsonObject);
		fThrownPlayerCoordinate = IServerJsonOption.THROWN_PLAYER_COORDINATE.getFrom(game, jsonObject);
		fThrowScatter = IServerJsonOption.THROW_SCATTER.getFrom(game, jsonObject);
		return this;
	}

}
