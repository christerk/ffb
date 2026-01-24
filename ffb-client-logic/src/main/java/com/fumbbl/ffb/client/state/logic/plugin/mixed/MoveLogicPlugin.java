package com.fumbbl.ffb.client.state.logic.plugin.mixed;

import com.fumbbl.ffb.RulesCollection;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2016)
public class MoveLogicPlugin extends com.fumbbl.ffb.client.state.logic.plugin.MoveLogicPlugin {

	@Override
	public Type getType() {
		return Type.MOVE;
	}
}
