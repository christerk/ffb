package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import com.balancedbytes.games.ffb.InducementPhase;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;

/**
 * Step to end kickoff sequence.
 * 
 * Pushes endTurnSequence and selectSequence on stack when finishing.
 * 
 * @author Kalimar
 */
public final class StepEndKickoff extends AbstractStep {
	
	public StepEndKickoff(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.END_KICKOFF;
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
  	SequenceGenerator.getInstance().pushEndTurnSequence(getGameState());
  	SequenceGenerator.getInstance().pushInducementSequence(getGameState(), InducementPhase.AFTER_KICKOFF_TO_OPPONENT_RESOLVED, game.isHomePlaying());
  	getResult().setNextAction(StepAction.NEXT_STEP);
  }
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
}
