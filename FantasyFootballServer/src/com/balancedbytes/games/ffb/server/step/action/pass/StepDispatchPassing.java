package com.balancedbytes.games.ffb.server.step.action.pass;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * Step in the pass sequence dispatching according to different types of passing.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END.
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_HAIL_MARY_PASS.
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_HAND_OVER.
 * 
 * Expects stepParameter CATCHER_ID to be set by a preceding step.
 * 
 * @author Kalimar
 */
public final class StepDispatchPassing extends AbstractStep {
	
	protected String fGotoLabelOnEnd;
	protected String fGotoLabelOnHailMaryPass;
	protected String fGotoLabelOnHandOver;

	private String fCatcherId;

	public StepDispatchPassing(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.DISPATCH_PASSING;
	}
	
  @Override
  public void init(StepParameterSet pParameterSet) {
  	if (pParameterSet != null) {
  		for (StepParameter parameter : pParameterSet.values()) {
  			switch (parameter.getKey()) {
  			  // mandatory
  				case GOTO_LABEL_ON_END:
  					fGotoLabelOnEnd = (String) parameter.getValue();
  					break;
  			  // mandatory
  				case GOTO_LABEL_ON_HAIL_MARY_PASS:
  					fGotoLabelOnHailMaryPass = (String) parameter.getValue();
  					break;
  			  // mandatory
  				case GOTO_LABEL_ON_HAND_OVER:
  					fGotoLabelOnHandOver = (String) parameter.getValue();
  					break;
					default:
						break;
  			}
  		}
  	}
  	if (!StringTool.isProvided(fGotoLabelOnEnd)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
  	}
  	if (!StringTool.isProvided(fGotoLabelOnHailMaryPass)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_HAIL_MARY_PASS + " is not initialized.");
  	}
  	if (!StringTool.isProvided(fGotoLabelOnHandOver)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_HAND_OVER + " is not initialized.");
  	}
  }

  @Override
  public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
	  	switch (pParameter.getKey()) {
				case CATCHER_ID:
					fCatcherId = (String) pParameter.getValue();
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
    if ((game.getThrower() == null) || (game.getThrowerAction() == null)) {
    	return;
    }
    switch (game.getThrowerAction()) {
    	case PASS:
    	case THROW_BOMB:
    	case DUMP_OFF:
    		getResult().setNextAction(StepAction.NEXT_STEP);
			  return;
    	case HAIL_MARY_PASS:
    	case HAIL_MARY_BOMB:
    		getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnHailMaryPass);
    		return;
    	case HAND_OVER:
      	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnHandOver);
      	return;
    	default:
        getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
        return;
    }
  }
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }

  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fGotoLabelOnEnd);
  	pByteList.addString(fGotoLabelOnHailMaryPass);
  	pByteList.addString(fGotoLabelOnHandOver);
  	pByteList.addString(fCatcherId);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnEnd = pByteArray.getString();
  	fGotoLabelOnHailMaryPass = pByteArray.getString();
  	fGotoLabelOnHandOver = pByteArray.getString();
  	fCatcherId = pByteArray.getString();
  	return byteArraySerializationVersion;
  }
  
}
