package com.fumbbl.ffb.injury.context;

import com.fumbbl.ffb.injury.InjuryType;

import java.util.Set;

public class ReRollSingleArmourDie extends InjuryContextModification {

	public ReRollSingleArmourDie(Set<Class<? extends InjuryType>> validInjuryTypes) {
		super(validInjuryTypes);
	}

	@Override
	boolean modifyArmourInternal(InjuryContext injuryContext) {

		return false;
	}

	@Override
	boolean modifyInjuryInternal(InjuryContext injuryContext) {

		return false;
	}
}
