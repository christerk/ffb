package com.balancedbytes.games.ffb.server.step.action.ttm;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Final step of the throw team mate sequence.
 * Consumes all expected stepParameters.
 * 
 * Expects stepParameter END_PLAYER_ACTION to be set by a preceding step.
 * Expects stepParameter END_TURN to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_COORDINATE to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_HAS_BALL to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_ID to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_STATE to be set by a preceding step.
 * 
 * @author Kalimar
 */
public final class StepEndThrowTeamMate extends AbstractStep {

  private boolean fEndTurn;
  private boolean fEndPlayerAction;
  private FieldCoordinate fThrownPlayerCoordinate;
	private boolean fThrownPlayerHasBall;
	private String fThrownPlayerId;
	private PlayerState fThrownPlayerState;

	public StepEndThrowTeamMate(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.END_THROW_TEAM_MATE;
	}
	
  @Override
  public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
	  	switch (pParameter.getKey()) {
				case END_TURN:
					fEndTurn = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
					pParameter.consume();
					return true;
				case THROWN_PLAYER_COORDINATE:
					fThrownPlayerCoordinate = (FieldCoordinate) pParameter.getValue();
					pParameter.consume();
					return true;
				case THROWN_PLAYER_HAS_BALL:
					fThrownPlayerHasBall = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
					pParameter.consume();
					return true;
				case THROWN_PLAYER_ID:
					fThrownPlayerId = (String) pParameter.getValue();
					pParameter.consume();
					return true;
				case THROWN_PLAYER_STATE:
					fThrownPlayerState = (PlayerState) pParameter.getValue();
					pParameter.consume();
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
				case CLIENT_ACTING_PLAYER:
					SequenceGenerator.getInstance().pushSelectSequence(getGameState(), false);
					getResult().setNextAction(StepAction.NEXT_STEP_AND_REPEAT);
					commandStatus = StepCommandStatus.SKIP_STEP;
					break;
				default:
					break;
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		UtilServerDialog.hideDialog(getGameState());
    Game game = getGameState().getGame();
    game.setPassCoordinate(null);
    game.getFieldModel().setRangeRuler(null);
    // reset thrown player (e.g. failed confusion roll, successful escape roll)
    Player thrownPlayer = game.getPlayerById(fThrownPlayerId);
    if ((thrownPlayer != null) && (fThrownPlayerCoordinate != null) && (fThrownPlayerState != null) && (fThrownPlayerState.getId() > 0)) {
    	game.getFieldModel().setPlayerCoordinate(thrownPlayer, fThrownPlayerCoordinate);
    	game.getFieldModel().setPlayerState(thrownPlayer, fThrownPlayerState);
    	if (fThrownPlayerHasBall) {
    		game.getFieldModel().setBallCoordinate(fThrownPlayerCoordinate);
    	}
    }
   	SequenceGenerator.getInstance().pushEndPlayerActionSequence(getGameState(), true, fEndTurn);
    getResult().setNextAction(StepAction.NEXT_STEP);
  }
	
	// ByteArray serialization
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fEndTurn = pByteArray.getBoolean();
  	fEndPlayerAction = pByteArray.getBoolean();
  	fThrownPlayerCoordinate = pByteArray.getFieldCoordinate();
  	fThrownPlayerHasBall = pByteArray.getBoolean();
  	fThrownPlayerId = pByteArray.getString();
  	int thrownPlayerStateId = pByteArray.getSmallInt();
  	fThrownPlayerState = (thrownPlayerStateId > 0) ? new PlayerState(thrownPlayerStateId) : null;
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
    IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
    IServerJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, fThrownPlayerId);
    IServerJsonOption.THROWN_PLAYER_STATE.addTo(jsonObject, fThrownPlayerState);
    IServerJsonOption.THROWN_PLAYER_HAS_BALL.addTo(jsonObject, fThrownPlayerHasBall);
    IServerJsonOption.THROWN_PLAYER_COORDINATE.addTo(jsonObject, fThrownPlayerCoordinate);
    return jsonObject;
  }
  
  @Override
  public StepEndThrowTeamMate initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fEndTurn = IServerJsonOption.END_TURN.getFrom(jsonObject);
    fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(jsonObject);
    fThrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(jsonObject);
    fThrownPlayerState = IServerJsonOption.THROWN_PLAYER_STATE.getFrom(jsonObject);
    fThrownPlayerHasBall = IServerJsonOption.THROWN_PLAYER_HAS_BALL.getFrom(jsonObject);
    fThrownPlayerCoordinate = IServerJsonOption.THROWN_PLAYER_COORDINATE.getFrom(jsonObject);
    return this;
  }

}
