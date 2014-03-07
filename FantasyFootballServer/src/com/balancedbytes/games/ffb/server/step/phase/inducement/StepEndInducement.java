package com.balancedbytes.games.ffb.server.step.phase.inducement;

import com.balancedbytes.games.ffb.InducementPhase;
import com.balancedbytes.games.ffb.InducementPhaseFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.UtilServerSteps;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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
	
  private boolean fEndInducementPhase;
  private boolean fEndTurn;
	private InducementPhase fInducementPhase;
	private boolean fHomeTeam;
	
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
		UtilServerDialog.hideDialog(getGameState());
		if (fInducementPhase == null) {
			return;
		}
    fEndTurn |= UtilServerSteps.checkTouchdown(getGameState());
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
	
	// ByteArray serialization
	
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fEndInducementPhase = pByteArray.getBoolean();
  	fEndTurn = pByteArray.getBoolean();
  	fInducementPhase = new InducementPhaseFactory().forId(pByteArray.getByte());
  	fHomeTeam = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.INDUCEMENT_PHASE.addTo(jsonObject, fInducementPhase);
    IServerJsonOption.HOME_TEAM.addTo(jsonObject, fHomeTeam);
    IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
    IServerJsonOption.END_INDUCEMENT_PHASE.addTo(jsonObject, fEndInducementPhase);
    return jsonObject;
  }
  
  @Override
  public StepEndInducement initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fInducementPhase = (InducementPhase) IServerJsonOption.INDUCEMENT_PHASE.getFrom(jsonObject);
    fHomeTeam = IServerJsonOption.HOME_TEAM.getFrom(jsonObject);
    fEndTurn = IServerJsonOption.END_TURN.getFrom(jsonObject);
    fEndInducementPhase = IServerJsonOption.END_INDUCEMENT_PHASE.getFrom(jsonObject);
    return this;
  }

}
