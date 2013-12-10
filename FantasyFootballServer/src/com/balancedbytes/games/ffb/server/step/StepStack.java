package com.balancedbytes.games.ffb.server.step;

import java.util.LinkedList;
import java.util.List;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class StepStack implements IByteArraySerializable, IJsonSerializable {
	
	private List<IStep> fStack;

	private transient GameState fGameState;

	public StepStack(GameState pGameState) {
		fGameState = pGameState;
		fStack = new LinkedList<IStep>();
	}
	
	public GameState getGameState() {
	  return fGameState;
  }
	
	public void push(IStep pStep) {
		fStack.add(0, pStep);
	}
	
	public void push(List<IStep> pSteps) {
		if ((pSteps != null) && (pSteps.size() > 0)) {
			for (int i = pSteps.size() - 1; i >= 0; i--) {
				push(pSteps.get(i));
			}
		}
	}

	public IStep pop() {
		if (size() > 0) {
			return fStack.remove(0);
		} else {
			return null;
		}
	}
	
	public int size() {
		return fStack.size();
	}
	
	public IStep[] toArray() {
		return fStack.toArray(new IStep[fStack.size()]);
	}
	
	public void clear() {
		fStack.clear();
	}
	
	public void publishStepParameter(StepParameter pParameter) {
		for (IStep step : fStack) {
			step.setParameter(pParameter);
			if (pParameter.isConsumed()) {
				break;
			}
		}
	}
	
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addSmallInt(fStack.size());
    for (IStep step : fStack) {
    	step.addTo(pByteList);
    }
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fStack.clear();
    int nrOfSteps = pByteArray.getSmallInt();
    for (int i = 0; i < nrOfSteps; i++) {
    	fStack.add(initStepFrom(pByteArray));
    }
    return byteArraySerializationVersion;
  }
  
  private IStep initStepFrom(ByteArray pByteArray) {
		StepId stepId = new StepIdFactory().forId(pByteArray.getSmallInt(0));
		IStep step = new StepFactory(getGameState()).forStepId(stepId);
		step.initFrom(pByteArray);
		return step;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    JsonArray stepArray = new JsonArray();
    for (IStep step : fStack) {
      stepArray.add(step.toJsonValue());
    }
    IServerJsonOption.STEPS.addTo(jsonObject, stepArray);
    return jsonObject;
  }
  
  public StepStack initFrom(JsonValue pJsonValue) {
    StepFactory stepFactory = new StepFactory(getGameState());
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    JsonArray stepArray = IServerJsonOption.STEPS.getFrom(jsonObject);
    fStack.clear();
    if (stepArray != null) {
      for (int i = 0; i < stepArray.size(); i++) {
        IStep step = stepFactory.forJsonValue(stepArray.get(i));
        fStack.add(step);
      }
    }
    return this;
  }
	
}
