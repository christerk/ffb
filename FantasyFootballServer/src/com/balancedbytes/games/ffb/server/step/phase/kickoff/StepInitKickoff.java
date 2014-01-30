package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import com.balancedbytes.games.ffb.InducementPhase;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.server.util.UtilGame;
import com.eclipsesource.json.JsonValue;

/**
 * Step to init the kickoff sequence.
 * 
 * @author Kalimar
 */
public final class StepInitKickoff extends AbstractStep {
	
	public StepInitKickoff(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.INIT_KICKOFF;
	}
	
	@Override
	public void start() {
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
    if (game.getTurnMode() == TurnMode.START_GAME) {
      UtilDialog.hideDialog(getGameState());
      UtilGame.startHalf(this, 1);
      game.setTurnMode(TurnMode.SETUP);
      game.startTurn();
      UtilGame.updateLeaderReRolls(this);
    }
    SequenceGenerator.getInstance().pushInducementSequence(getGameState(), InducementPhase.BEFORE_SETUP, game.isHomePlaying());
    getResult().setNextAction(StepAction.NEXT_STEP);
  }
  
  // ByteArray serialization
    
  public int getByteArraySerializationVersion() {
  	return 2;
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	if (byteArraySerializationVersion < 2) {
    	pByteArray.getString();
    	pByteArray.getFieldCoordinate();
    	pByteArray.getBoolean();
  	}
  	return byteArraySerializationVersion;
  }

  // JSON serialization
    
  @Override
  public StepInitKickoff initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    return this;
  }
  
}
