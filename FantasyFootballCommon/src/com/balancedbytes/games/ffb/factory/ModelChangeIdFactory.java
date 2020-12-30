package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.model.change.ModelChangeId;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.MODEL_CHANGE_ID)
@RulesCollection(Rules.COMMON)
public class ModelChangeIdFactory implements INamedObjectFactory {

	public ModelChangeId forName(String pName) {
		for (ModelChangeId changeId : ModelChangeId.values()) {
			if (changeId.getName().equalsIgnoreCase(pName)) {
				return changeId;
			}
		}
		return null;
	}

	@Override
	public void initialize(Rules rules, GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
