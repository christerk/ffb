package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.ANIMATION_TYPE)
@RulesCollection(Rules.COMMON)
public class AnimationTypeFactory implements INamedObjectFactory {

	public AnimationType forName(String pName) {
		for (AnimationType type : AnimationType.values()) {
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
