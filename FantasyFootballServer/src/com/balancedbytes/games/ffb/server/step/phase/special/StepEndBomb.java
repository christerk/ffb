package com.balancedbytes.games.ffb.server.step.phase.special;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.UtilSteps;

/**
 * Final step of the bomb sequence.
 * Consumes all expected stepParameters.
 * 
 * Expects stepParameter CATCHER_ID to be set by a preceding step.
 * Expects stepParameter END_TURN to be set by a preceding step.
 *
 * @author Kalimar
 */
public final class StepEndBomb extends AbstractStep {

	protected String fCatcherId;
	protected boolean fEndTurn;
	
	public StepEndBomb(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.END_BOMB;
	}
	
	
  @Override
  public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
	  	switch (pParameter.getKey()) {
	  		case CATCHER_ID:
	  			fCatcherId = (String) pParameter.getValue();
	  			pParameter.consume();
	  			return true;
		  	case END_TURN:
  				fEndTurn = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
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
	
  private void executeStep() {
    Game game = getGameState().getGame();
		game.setPassCoordinate(null);
  	fEndTurn |= UtilSteps.checkTouchdown(getGameState());
    if (fEndTurn || (fCatcherId == null)) {
  		game.setHomePlaying((TurnMode.BOMB_HOME == game.getTurnMode()) || (TurnMode.BOMB_HOME_BLITZ == game.getTurnMode()));
    	if ((TurnMode.BOMB_HOME_BLITZ == game.getTurnMode()) || (TurnMode.BOMB_AWAY_BLITZ == game.getTurnMode())) {
    		game.setTurnMode(TurnMode.BLITZ);
    	} else {
    		game.setTurnMode(TurnMode.REGULAR);
    	}
    	SequenceGenerator.getInstance().pushEndPlayerActionSequence(getGameState(), false, fEndTurn);
    } else {
    	Player catcher = game.getPlayerById(fCatcherId);
    	game.setHomePlaying(game.getTeamHome().hasPlayer(catcher));
    	UtilSteps.changePlayerAction(this, fCatcherId, PlayerAction.THROW_BOMB, false);
    	SequenceGenerator.getInstance().pushPassSequence(getGameState());
    }
  	getResult().setNextAction(StepAction.NEXT_STEP);
	}

  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fCatcherId);
  	pByteList.addBoolean(fEndTurn);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fCatcherId = pByteArray.getString();
  	fEndTurn = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }

}
