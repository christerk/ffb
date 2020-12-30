package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.change.ModelChangeDataType;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.MODEL_CHANGE_DATA_TYPE)
@RulesCollection(Rules.COMMON)
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
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
