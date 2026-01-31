package com.fumbbl.ffb.client.state.logic.plugin.mixed;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2016)
public class BaseLogicPlugin extends com.fumbbl.ffb.client.state.logic.plugin.BaseLogicPlugin {
	@Override
	public boolean playerCanNotMove(PlayerState playerState) {
		return playerState.isRooted();
	}
}
