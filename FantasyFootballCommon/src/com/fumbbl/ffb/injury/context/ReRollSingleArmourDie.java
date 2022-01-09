package com.fumbbl.ffb.injury.context;

public class ReRollSingleArmourDie extends InjuryContextModification {

	@Override
	boolean modifyArmourInternal(InjuryContext injuryContext) {

		return false;
	}

	@Override
	boolean modifyInjuryInternal(InjuryContext injuryContext) {

		return false;
	}
}
