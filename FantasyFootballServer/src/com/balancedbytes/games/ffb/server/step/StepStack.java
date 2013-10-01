package com.balancedbytes.games.ffb.server.step;

import java.util.LinkedList;
import java.util.List;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.server.GameState;

/**
 * 
 * @author Kalimar
 */
public class StepStack implements IByteArraySerializable {
	
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
		StepId stepId = StepId.fromId(pByteArray.getSmallInt(0));
		IStep step = StepFactory.getInstance().create(stepId, getGameState(), null, null);
		step.initFrom(pByteArray);
		return step;
  }
	
}
