package com.balancedbytes.games.ffb.server.step.action.ttm;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.common.ApothecaryMode;
import com.balancedbytes.games.ffb.server.util.UtilInjury;

/**
 * Step in ttm sequence to eat a team mate.
 * 
 * Expects stepParameter THROWN_PLAYER_COORDINATE to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_ID to be set by a preceding step.
 * 
 * Sets stepParameter CATCH_SCATTER_THROW_IN_MODE for all steps on the stack.
 * Sets stepParameter END_TURN for all steps on the stack.
 * Sets stepParameter INJURY_RESULT for all steps on the stack.
 * Sets stepParameter THROWN_PLAYER_COORDINATE for all steps on the stack.
 * 
 * @author Kalimar
 */
public final class StepEatTeamMate extends AbstractStep {

	protected FieldCoordinate fThrownPlayerCoordinate;
	protected String fThrownPlayerId;

	public StepEatTeamMate(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.EAT_TEAM_MATE;
	}
	
  @Override
  public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
	  	switch (pParameter.getKey()) {
				case THROWN_PLAYER_COORDINATE:
					fThrownPlayerCoordinate = (FieldCoordinate) pParameter.getValue();
					return true;
				case THROWN_PLAYER_ID:
					fThrownPlayerId = (String) pParameter.getValue();
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
    if ((thrownPlayer != null) && (fThrownPlayerCoordinate != null)) {
	    if (fThrownPlayerCoordinate.equals(game.getFieldModel().getBallCoordinate())) {
	      game.getFieldModel().setBallMoving(true);
	      publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
	      publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
	    }
	    InjuryResult injuryResultThrownPlayer = UtilInjury.handleInjury(this, InjuryType.EAT_PLAYER, null, thrownPlayer, fThrownPlayerCoordinate, null, ApothecaryMode.THROWN_PLAYER);
	    publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultThrownPlayer));
	    publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null));  // avoid reset in end step
	    getResult().setSound(Sound.NOMNOM);
    }
    getResult().setNextAction(StepAction.NEXT_STEP);
  }
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }

  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addFieldCoordinate(fThrownPlayerCoordinate);
  	pByteList.addString(fThrownPlayerId);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fThrownPlayerCoordinate = pByteArray.getFieldCoordinate();
  	fThrownPlayerId = pByteArray.getString();
  	return byteArraySerializationVersion;
  }

}
