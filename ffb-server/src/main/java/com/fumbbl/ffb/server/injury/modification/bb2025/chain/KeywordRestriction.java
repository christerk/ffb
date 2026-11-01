package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.model.Keyword;
import com.fumbbl.ffb.model.Player;

public class KeywordRestriction implements ModificationRestriction {

	private final Keyword keyword;

	public KeywordRestriction(Keyword keyword) {
		this.keyword = keyword;
	}

	@Override
	public RestrictionType type() {
		return RestrictionType.KEYWORD;
	}

	@Override
	public boolean isMet(Player<?> defender) {
		return defender.getPosition() != null && defender.getPosition().getKeywords().contains(keyword);
	}

	public Keyword getKeyword() {
		return keyword;
	}
}
