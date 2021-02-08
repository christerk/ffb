package com.balancedbytes.games.ffb.server.step.action.move;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.TrackNumber;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.util.ServerUtilBlock;
import com.balancedbytes.games.ffb.server.util.UtilServerPlayerMove;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in move sequence to update player position (actually move).
 * 
 * Expects stepParameter COORDINATE_FROM to be set by a preceding step. Expects
 * stepParameter COORDINATE_TO to be set by a preceding step. Expects
 * stepParameter MOVE_STACK to be set by a preceding step.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class StepMove extends AbstractStep {

	private FieldCoordinate fCoordinateFrom;
	private FieldCoordinate fCoordinateTo;
	private int fMoveStackSize;

	public StepMove(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.MOVE;
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
			case MOVE_STACK:
				FieldCoordinate[] moveStack = (FieldCoordinate[]) pParameter.getValue();
				fMoveStackSize = ((moveStack != null) ? moveStack.length : 0);
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
		ActingPlayer actingPlayer = game.getActingPlayer();
		PlayerState playerState = game.getFieldModel().getPlayerState(game.getActingPlayer().getPlayer());
		if (!playerState.isRooted()) {
			TrackNumber trackNumber = new TrackNumber(fCoordinateFrom, actingPlayer.getCurrentMove());
			actingPlayer.setCurrentMove(game.getActingPlayer().getCurrentMove() + (actingPlayer.isLeaping() ? 2 : 1));
			game.getFieldModel().add(trackNumber);
			boolean ballPositionUpdated = game.getFieldModel().updatePlayerAndBallPosition(actingPlayer.getPlayer(),
					fCoordinateTo);
			if (ballPositionUpdated) {
				PlayerResult playerResult = game.getGameResult().getPlayerResult(game.getActingPlayer().getPlayer());
				int deltaX = 0;
				if (game.isHomePlaying()) {
					deltaX = fCoordinateTo.getX() - fCoordinateFrom.getX();
				} else {
					deltaX = fCoordinateFrom.getX() - fCoordinateTo.getX();
				}
				playerResult.setRushing(playerResult.getRushing() + deltaX);
			}
			actingPlayer.setGoingForIt(UtilPlayer.isNextMoveGoingForIt(game)); // auto go-for-it
			if (fMoveStackSize == 0) {
				UtilServerPlayerMove.updateMoveSquares(getGameState(), false);
			}
			ServerUtilBlock.updateDiceDecorations(game);
			getResult().setSound(actingPlayer.isDodging() ? SoundId.DODGE : SoundId.STEP);
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.COORDINATE_FROM.addTo(jsonObject, fCoordinateFrom);
		IServerJsonOption.COORDINATE_TO.addTo(jsonObject, fCoordinateTo);
		IServerJsonOption.MOVE_STACK_SIZE.addTo(jsonObject, fMoveStackSize);
		return jsonObject;
	}

	@Override
	public StepMove initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fCoordinateFrom = IServerJsonOption.COORDINATE_FROM.getFrom(game, jsonObject);
		fCoordinateTo = IServerJsonOption.COORDINATE_TO.getFrom(game, jsonObject);
		fMoveStackSize = IServerJsonOption.MOVE_STACK_SIZE.getFrom(game, jsonObject);
		return this;
	}

}
