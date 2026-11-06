package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

/**
 * BUILD: player state dependent conditions are evaluated as if the affected players were standing (tree construction).
 * OFFER: all conditions are checked on the real game (before an option is shown).
 */
public enum EligibilityMode {
	BUILD, OFFER
}
