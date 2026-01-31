package com.fumbbl.ffb.client.state.logic.plugin.bb2025;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;

@RulesCollection(RulesCollection.Rules.BB2025)
public class BaseLogicPlugin extends com.fumbbl.ffb.client.state.logic.plugin.BaseLogicPlugin {
	@Override
	public boolean playerCanNotMove(PlayerState playerState) {
		return playerState.isPinned();
	}
}
