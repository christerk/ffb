package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.StringTool;

@FactoryType(FactoryType.Factory.PASS_RESULT)
@RulesCollection(RulesCollection.Rules.COMMON)
public class PassResultFactory implements INamedObjectFactory<PassResult> {

	@Override
	public PassResult forName(String pName) {
		if (StringTool.isProvided(pName)) {
			return PassResult.valueOf(pName);
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
	}
}
