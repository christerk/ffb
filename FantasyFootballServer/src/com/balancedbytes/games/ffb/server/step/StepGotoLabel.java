package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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

	// ByteArray serialization
	
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
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL.addTo(jsonObject, fGotoLabel);
    return jsonObject;
  }
  
  public StepGotoLabel initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabel = IServerJsonOption.GOTO_LABEL.getFrom(jsonObject);
    return this;
  }

}
