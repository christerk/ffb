package com.balancedbytes.games.ffb.server.step.phase.inducement;

import com.balancedbytes.games.ffb.InducementPhase;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.UtilSteps;
import com.balancedbytes.games.ffb.server.util.UtilDialog;

/**
 * Last step in the inducement sequence.
 * Consumes all expected stepParameters.
 * 
 * Expects stepParameter END_INDUCEMENT_PHASE to be set by a preceding step.
 * Expects stepParameter END_TURN to be set by a preceding step.
 * Expects stepParameter HOME_TEAM to be set by a preceding step.
 * Expects stepParameter INDUCEMENT_PHASE to be set by a preceding step.
 * 
 * @author Kalimar
 */
public final class StepEndInducement extends AbstractStep {
	
	protected boolean fEndInducementPhase;
	protected boolean fEndTurn;
	protected InducementPhase fInducementPhase;
	protected boolean fHomeTeam;
	
	public StepEndInducement(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.END_INDUCEMENT;
	}
		
	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
  			case HOME_TEAM:
  				fHomeTeam = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
  				pParameter.consume();
  				return true;
				case INDUCEMENT_PHASE:
					fInducementPhase = (InducementPhase) pParameter.getValue();
					pParameter.consume();
					return true;
				case END_INDUCEMENT_PHASE:
					fEndInducementPhase = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
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
		UtilDialog.hideDialog(getGameState());
		if (fInducementPhase == null) {
			return;
		}
    fEndTurn |= UtilSteps.checkTouchdown(getGameState());
    if (fEndTurn) {
    	SequenceGenerator.getInstance().pushEndTurnSequence(getGameState());
    } else if (fEndInducementPhase) {
    	switch (fInducementPhase) {
    		case END_OF_OWN_TURN:
      		SequenceGenerator.getInstance().pushEndTurnSequence(getGameState());
      		break;
    		case START_OF_OWN_TURN:
        	SequenceGenerator.getInstance().pushSelectSequence(getGameState(), true);
        	break;
      	default:
      		break;
    	}
    } else {
    	SequenceGenerator.getInstance().pushInducementSequence(getGameState(), fInducementPhase, fHomeTeam);
    }
  	getResult().setNextAction(StepAction.NEXT_STEP);
  }
	
	public int getByteArraySerializationVersion() {
  	return 1;
  }
  
	@Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addBoolean(fEndInducementPhase);
  	pByteList.addBoolean(fEndTurn);
  	pByteList.addByte((byte) ((fInducementPhase != null) ? fInducementPhase.getId() : 0));
  	pByteList.addBoolean(fHomeTeam);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fEndInducementPhase = pByteArray.getBoolean();
  	fEndTurn = pByteArray.getBoolean();
  	fInducementPhase = InducementPhase.fromId(pByteArray.getByte());
  	fHomeTeam = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }

}
