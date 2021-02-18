package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.mechanics.PassResult;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.StringTool;

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
