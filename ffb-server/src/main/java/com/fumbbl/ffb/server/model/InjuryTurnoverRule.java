package com.fumbbl.ffb.server.model;

/**
 * How a dropped player's final injury decides whether the turn ends. Needed when the injury can still change by
 * modifications after the drop context was created.
 */
public enum InjuryTurnoverRule {
	/**
	 * The end turn flag of the drop context is used as is.
	 */
	FIXED,
	/**
	 * Chainsaw kick back during a block: broken armour ends the turn unless chainsaw turnovers are disabled (always with
	 * the ball), holding armour only ends it with the kick back option.
	 */
	CHAINSAW_BLOCK,
	/**
	 * Chainsaw kick back during a foul: the ball always ends the turn, otherwise like {@link #CHAINSAW_BLOCK}.
	 */
	CHAINSAW_FOUL
}
