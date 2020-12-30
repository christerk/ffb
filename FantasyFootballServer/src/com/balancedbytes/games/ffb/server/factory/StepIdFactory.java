package com.balancedbytes.games.ffb.server.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.factory.INamedObjectFactory;
import com.balancedbytes.games.ffb.model.GameOptions;
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
	public void initialize(Rules rules, GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
