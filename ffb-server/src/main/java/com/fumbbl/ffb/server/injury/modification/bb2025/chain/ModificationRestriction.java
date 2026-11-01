package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.model.Player;

public interface ModificationRestriction {

	RestrictionType type();

	boolean isMet(Player<?> defender);
}
