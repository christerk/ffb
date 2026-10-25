package com.fumbbl.ffb.injury.context;

public interface IInjuryContextModification {
	boolean requiresConditionalReRollSkill();

	default boolean appliesToDefender() {
		return false;
	}

	/**
	 * @return whether foul assists must not be applied to the armour roll of the modified context
	 */
	default boolean ignoresFoulAssists() {
		return false;
	}
}
