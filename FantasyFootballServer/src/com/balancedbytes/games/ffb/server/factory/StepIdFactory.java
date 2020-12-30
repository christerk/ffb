package com.balancedbytes.games.ffb.server.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.factory.INamedObjectFactory;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.step.StepId;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.STEP_ID)
public class StepIdFactory implements INamedObjectFactory {

	public StepId forName(String pName) {
		for (StepId stepId : StepId.values()) {
			if (stepId.getName().equalsIgnoreCase(pName)) {
				return stepId;
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
