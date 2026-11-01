package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.injury.context.InjuryContext;

/**
 * Copies the parts of an injury context that are relevant for armour and injury evaluation, used for side effect
 * free simulations of modifications.
 */
public class InjuryContextCopyService {

	public InjuryContext copy(InjuryContext source) {
		InjuryContext copy = new InjuryContext();
		copy.fInjuryType = source.fInjuryType;
		copy.fAttackerId = source.fAttackerId;
		copy.fDefenderId = source.fDefenderId;
		copy.fDefenderPosition = source.fDefenderPosition;
		copy.fArmorModifiers.addAll(source.fArmorModifiers);
		copy.fInjuryModifiers.addAll(source.fInjuryModifiers);
		copy.casualtyModifiers.addAll(source.casualtyModifiers);
		copy.fArmorBroken = source.fArmorBroken;
		copy.fArmorRoll = copyRoll(source.fArmorRoll);
		copy.fInjuryRoll = copyRoll(source.fInjuryRoll);
		copy.fInjury = source.fInjury;
		copy.fApothecaryMode = source.fApothecaryMode;
		copy.fApothecaryStatus = source.fApothecaryStatus;
		return copy;
	}

	private int[] copyRoll(int[] roll) {
		return roll == null ? null : roll.clone();
	}
}
