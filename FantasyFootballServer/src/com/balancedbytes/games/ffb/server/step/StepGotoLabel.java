package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * Step in any sequence to jump to a given label.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL.
 * 
 * @author Kalimar
 */
public class StepGotoLabel extends AbstractStep {
	
	private String fGotoLabel;
	
	public StepGotoLabel(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.GOTO_LABEL;
	}
	
	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					case GOTO_LABEL:
						fGotoLabel = (String) parameter.getValue();
						break;
					default:
						break;
				}
			}
		}
		if (!StringTool.isProvided(fGotoLabel)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL + " is not initialized.");
		}
	}

	@Override
	public void start() {
		super.start();
		getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabel);
	}

  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fGotoLabel);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabel = pByteArray.getString();
  	return byteArraySerializationVersion;
  }

}
