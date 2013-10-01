package com.balancedbytes.games.ffb.server.step.action.ttm;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.util.UtilDialog;

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

	protected boolean fEndTurn;
	protected boolean fEndPlayerAction;
	protected FieldCoordinate fThrownPlayerCoordinate;
	protected boolean fThrownPlayerHasBall;
	protected String fThrownPlayerId;
	protected PlayerState fThrownPlayerState;

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
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand) {
		StepCommandStatus commandStatus = super.handleNetCommand(pNetCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pNetCommand.getId()) {
				case CLIENT_ACTING_PLAYER:
					SequenceGenerator.getInstance().pushSelectSequence(getGameState(), false);
					getResult().setNextAction(StepAction.NEXT_STEP_AND_REPEAT_COMMAND);
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
		UtilDialog.hideDialog(getGameState());
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
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }

  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addBoolean(fEndTurn);
  	pByteList.addBoolean(fEndPlayerAction);
  	pByteList.addFieldCoordinate(fThrownPlayerCoordinate);
  	pByteList.addBoolean(fThrownPlayerHasBall);
  	pByteList.addString(fThrownPlayerId);
  	pByteList.addSmallInt((fThrownPlayerState != null) ? fThrownPlayerState.getId() : 0);
  }
  
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

}
