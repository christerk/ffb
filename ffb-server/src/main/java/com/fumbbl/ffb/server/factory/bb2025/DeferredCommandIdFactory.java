package com.fumbbl.ffb.server.factory.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.step.DeferredCommandId;


@FactoryType(FactoryType.Factory.DEFERRED_COMMAND_ID)
@RulesCollection(RulesCollection.Rules.BB2025)
public class DeferredCommandIdFactory implements INamedObjectFactory<DeferredCommandId> {

	public DeferredCommandId forName(String pName) {
		for (DeferredCommandId stepId : DeferredCommandId.values()) {
			if (stepId.getName().equalsIgnoreCase(pName)) {
				return stepId;
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
	}

}
