package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.change.ModelChangeId;

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
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
