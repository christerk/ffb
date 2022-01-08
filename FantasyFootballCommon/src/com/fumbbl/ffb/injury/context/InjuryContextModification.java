package com.fumbbl.ffb.injury.context;

public abstract class InjuryContextModification {

	public void modifyArmour(InjuryContext injuryContext) {
		if (injuryContext.getAlternateInjuryContext() != null) {
			modifyArmourInternal(injuryContext);
		}
	}

	public void modifyInjury(InjuryContext injuryContext) {
		if (injuryContext.getAlternateInjuryContext() != null) {
			modifyArmourInternal(injuryContext);
		}
	}

	abstract void modifyArmourInternal(InjuryContext injuryContext);

	abstract void modifyInjuryInternal(InjuryContext injuryContext);
}
