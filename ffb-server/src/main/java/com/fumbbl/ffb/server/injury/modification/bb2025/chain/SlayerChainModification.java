package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.model.Keyword;
import com.fumbbl.ffb.model.Player;

/**
 * Like {@link AvOrInjChainModification} but only against Big Guys.
 */
public class SlayerChainModification extends AvOrInjChainModification {

	@Override
	public ModificationRestriction restriction() {
		return ModificationRestriction.KEYWORD;
	}

	@Override
	public boolean isApplicable(InjuryChainContext context, InjuryNode node, InjuryPhase phase, String ownerId,
		EligibilityMode mode) {
		Player<?> defender = context.getDefender(node);
		return defender != null && defender.getPosition().getKeywords().contains(Keyword.BIG_GUY)
			&& super.isApplicable(context, node, phase, ownerId, mode);
	}
}
