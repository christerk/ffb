package com.fumbbl.ffb.server.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.step.StepAction;

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
