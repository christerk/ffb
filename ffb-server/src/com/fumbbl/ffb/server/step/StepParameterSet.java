package com.fumbbl.ffb.server.step;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Kalimar
 */
public class StepParameterSet {

	private Map<StepParameterKey, StepParameter> fParameterById;

	public StepParameterSet() {
		fParameterById = new HashMap<>();
	}

	public void add(StepParameter pStepParameter) {
		fParameterById.put(pStepParameter.getKey(), pStepParameter);
	}

	public StepParameter get(StepParameterKey pKey) {
		return fParameterById.get(pKey);
	}

	public boolean remove(StepParameterKey pKey) {
		return fParameterById.remove(pKey) != null;
	}

	public int getSize() {
		return fParameterById.size();
	}

	public StepParameter[] values() {
		return fParameterById.values().toArray(new StepParameter[0]);
	}

}
