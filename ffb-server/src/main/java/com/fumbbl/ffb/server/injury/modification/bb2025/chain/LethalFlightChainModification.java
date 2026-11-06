package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.injury.TTMHitPlayerForSpp;

import java.util.Collections;

/**
 * BB2025 Lethal Flight with {@code LETHAL_FLIGHT_OPTIONAL}: +1 to the armour or the injury roll of an opponent hit by
 * the thrown player.
 */
public class LethalFlightChainModification extends OptionalSkillChainModification {

	public LethalFlightChainModification() {
		super(Collections.singleton(TTMHitPlayerForSpp.class));
	}

	@Override
	protected boolean isTtm() {
		return true;
	}
}
