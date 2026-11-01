package com.fumbbl.ffb.injury.context;

import com.fumbbl.ffb.injury.InjuryType;

public interface IInjuryContextModification {
	boolean isValidType(InjuryType injuryType);

	boolean requiresConditionalReRollSkill();

	default boolean appliesToDefender() {
		return false;
	}
}
