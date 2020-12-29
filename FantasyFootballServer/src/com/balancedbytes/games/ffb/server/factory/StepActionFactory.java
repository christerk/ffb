package com.balancedbytes.games.ffb.server.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.factory.INamedObjectFactory;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.server.step.StepAction;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.stepAction)
public class StepActionFactory implements INamedObjectFactory {

	public StepAction forName(String pName) {
		for (StepAction stepAction : StepAction.values()) {
			if (stepAction.getName().equalsIgnoreCase(pName)) {
				return stepAction;
			}
		}
		return null;
	}

	@Override
	public void initialize(Rules rules, GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
