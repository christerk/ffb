package com.balancedbytes.games.ffb.server.step;

/**
 * 
 * @author Kalimar
 */
public final class StepParameter {

	private StepParameterKey fKey;
	private Object fValue;
	private boolean fConsumed;

	public StepParameter(StepParameterKey pKey, Object pValue) {
		fKey = pKey;
		fValue = pValue;
	}

	public StepParameterKey getKey() {
		return fKey;
	}

	public Object getValue() {
		return fValue;
	}

	public boolean isConsumed() {
		return fConsumed;
	}

	public void consume() {
		fConsumed = true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fKey == null) ? 0 : fKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StepParameter other = (StepParameter) obj;
		if (fKey != other.fKey)
			return false;
		return true;
	}

}
