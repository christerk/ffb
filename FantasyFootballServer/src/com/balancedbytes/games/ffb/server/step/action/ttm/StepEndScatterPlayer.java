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

/**
 * Step to end ttm scatter sequence.
 * 
 * Expects stepParameter THROWN_PLAYER_ID to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_STATE to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_HAS_BALL to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_COORDINATE to be set by a preceding step.
 *
 * Consumes all known parameters.
 * May push new scatterPlayerSequence on the stack.
 * 
 * @author Kalimar
 */
public final class StepEndScatterPlayer extends AbstractStep {

	protected String fThrownPlayerId;
	protected boolean fThrownPlayerHasBall;
	protected PlayerState fThrownPlayerState;
	protected FieldCoordinate fThrownPlayerCoordinate;

	public StepEndScatterPlayer(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.END_SCATTER_PLAYER;
	}
	
  @Override
  public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
	  	switch (pParameter.getKey()) {
				case THROWN_PLAYER_ID:
					fThrownPlayerId = (String) pParameter.getValue();
					pParameter.consume();
					return true;
				case THROWN_PLAYER_HAS_BALL:
					fThrownPlayerHasBall = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
					pParameter.consume();
					return true;
				case THROWN_PLAYER_STATE:
					fThrownPlayerState = (PlayerState) pParameter.getValue();
					pParameter.consume();
					return true;
				case THROWN_PLAYER_COORDINATE:
					fThrownPlayerCoordinate = (FieldCoordinate) pParameter.getValue();
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
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
    Game game = getGameState().getGame();
    Player thrownPlayer = game.getPlayerById(fThrownPlayerId);
    if (thrownPlayer != null) {
    	if ((fThrownPlayerState != null) && (fThrownPlayerCoordinate != null)) {
    		SequenceGenerator.getInstance().pushScatterPlayerSequence(getGameState(), fThrownPlayerId, fThrownPlayerState, fThrownPlayerHasBall, fThrownPlayerCoordinate, false);
    	}
    }
  	getResult().setNextAction(StepAction.NEXT_STEP);
  }
	
  public int getByteArraySerializationVersion() {
  	return 1;
  }

  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fThrownPlayerId);
  	pByteList.addSmallInt((fThrownPlayerState != null) ? fThrownPlayerState.getId() : 0);
  	pByteList.addBoolean(fThrownPlayerHasBall);
  	pByteList.addFieldCoordinate(fThrownPlayerCoordinate);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fThrownPlayerId = pByteArray.getString();
  	fThrownPlayerState = new PlayerState(pByteArray.getSmallInt());
  	fThrownPlayerHasBall = pByteArray.getBoolean();
  	fThrownPlayerCoordinate = pByteArray.getFieldCoordinate();
  	return byteArraySerializationVersion;
  }

}
