package com.fumbbl.ffb.client.state.logic.plugin.bb2025;

import com.fumbbl.ffb.RulesCollection;

@RulesCollection(RulesCollection.Rules.BB2025)
public class MoveLogicPlugin extends com.fumbbl.ffb.client.state.logic.plugin.MoveLogicPlugin {

	@Override
	public Type getType() {
		return Type.MOVE;
	}
}
