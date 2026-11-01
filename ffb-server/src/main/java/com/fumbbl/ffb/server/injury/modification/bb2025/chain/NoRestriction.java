package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.model.Player;

public class NoRestriction implements ModificationRestriction {

	@Override
	public RestrictionType type() {
		return RestrictionType.NONE;
	}

	@Override
	public boolean isMet(Player<?> defender) {
		return true;
	}
}
