package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;

import java.util.Arrays;

@FactoryType(FactoryType.Factory.RE_ROLL_SOURCE)
@RulesCollection(Rules.BB2025)
public class ReRollPropertyFactory implements INamedObjectFactory<ReRollProperty> {

	public ReRollProperty forName(String pName) {
		return Arrays.stream(ReRollProperty.values()).filter(prop -> prop.getName().equalsIgnoreCase(pName)).findFirst()
				.orElse(null);
	}

	@Override
	public void initialize(Game game) {
	}

}
