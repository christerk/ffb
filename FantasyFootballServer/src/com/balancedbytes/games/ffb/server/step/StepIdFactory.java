package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.INamedObjectFactory;

/**
 * 
 * @author Kalimar
 */
public class StepIdFactory implements INamedObjectFactory {

	public StepId forName(String pName) {
		for (StepId stepId : StepId.values()) {
			if (stepId.getName().equalsIgnoreCase(pName)) {
				return stepId;
			}
		}
		return null;
	}

}
