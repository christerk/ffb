package com.fumbbl.ffb.server.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.step.StepId;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.STEP_ID)
@RulesCollection(RulesCollection.Rules.COMMON)
public class StepIdFactory implements INamedObjectFactory<StepId> {

	public StepId forName(String pName) {
		for (StepId stepId : StepId.values()) {
			if (stepId.getName().equalsIgnoreCase(pName)) {
				return stepId;
			}
		}
		for (StepId stepId : StepId.values()) {
			if (stepId.getOldName().equalsIgnoreCase(pName)) {
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
