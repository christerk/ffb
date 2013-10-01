package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import com.balancedbytes.games.ffb.Team;
import com.balancedbytes.games.ffb.TurnMode;
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
import com.balancedbytes.games.ffb.server.util.UtilGame;
import com.balancedbytes.games.ffb.server.util.UtilTimer;

/**
 * Step in kickoff sequence to handle blitz kickoff result.
 * 
 * Expects stepParameter END_TURN to be set by a preceding step.
 *   (parameter is consumed on TurnMode.BLITZ)
 * 
 * @author Kalimar
 */
public final class StepBlitzTurn extends AbstractStep {
	
	protected boolean fEndTurn;
	
	public StepBlitzTurn(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.BLITZ_TURN;
	}
	
	@Override
	public boolean setParameter(StepParameter pParameter) {
    Game game = getGameState().getGame();
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case END_TURN:
					fEndTurn = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
					if (game.getTurnMode() == TurnMode.BLITZ) {
						pParameter.consume();
					}
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
  	
  	if (game.getTurnMode() == TurnMode.BLITZ) {
  		
  		if (fEndTurn) {
  			game.setTurnMode(TurnMode.KICKOFF);
  		}
  		
  	} else {
  		
  		game.setTurnMode(TurnMode.BLITZ);
	    Team blitzingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
	    UtilKickoffSequence.pinPlayersInTacklezones(getGameState(), blitzingTeam);
	    if (game.isTurnTimeEnabled()) {
	      UtilTimer.stopTurnTimer(getGameState());
	      game.setTurnTime(0);
	      UtilTimer.startTurnTimer(getGameState());
	    }
	    game.startTurn();
	    UtilGame.updateLeaderReRolls(this);
	    // insert select sequence into kickoff sequence after this step
	    getGameState().pushCurrentStepOnStack();
	    SequenceGenerator.getInstance().pushSelectSequence(getGameState(), true);
	    
  	}
  	
  	getResult().setNextAction(StepAction.NEXT_STEP);
  	
  }
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addBoolean(fEndTurn);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fEndTurn = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
}
