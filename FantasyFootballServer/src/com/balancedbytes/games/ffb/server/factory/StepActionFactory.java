package com.balancedbytes.games.ffb.server.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.factory.INamedObjectFactory;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.step.StepAction;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.STEP_ACTION)
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
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
