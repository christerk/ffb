package com.fumbbl.ffb.injury.context;

import com.fumbbl.ffb.injury.InjuryType;

public interface IInjuryContextModification {
	boolean requiresConditionalReRollSkill();

	default boolean appliesToDefender() {
		return false;
	}

	default boolean isValidType(InjuryType injuryType) {
		return true;
	}
}
