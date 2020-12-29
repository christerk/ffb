package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.model.change.ModelChangeDataType;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.modelChangeDataType)
public class ModelChangeDataTypeFactory implements INamedObjectFactory {

	public ModelChangeDataType forName(String pName) {
		for (ModelChangeDataType type : ModelChangeDataType.values()) {
			if (type.getName().equalsIgnoreCase(pName)) {
				return type;
			}
		}
		return null;
	}

	@Override
	public void initialize(Rules rules, GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
