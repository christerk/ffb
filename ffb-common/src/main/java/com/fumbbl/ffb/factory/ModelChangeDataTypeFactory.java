package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.change.ModelChangeDataType;

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
	}

}
