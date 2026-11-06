package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.injury.context.InjuryContext;

/**
 * Side effect free copies of injury contexts, the alternative context of the legacy mechanism is not copied.
 */
public class InjuryContextCopyService {

	public InjuryContext copy(InjuryContext source) {
		InjuryContext target = new InjuryContext();
		target.fInjuryType = source.fInjuryType;
		target.fDefenderId = source.fDefenderId;
		target.fDefenderPosition = source.fDefenderPosition;
		target.fAttackerId = source.fAttackerId;
		target.fArmorModifiers.addAll(source.fArmorModifiers);
		target.fArmorRoll = copy(source.fArmorRoll);
		target.fArmorBroken = source.fArmorBroken;
		target.fInjuryModifiers.addAll(source.fInjuryModifiers);
		target.fInjuryRoll = copy(source.fInjuryRoll);
		target.fCasualtyRoll = copy(source.fCasualtyRoll);
		target.fCasualtyRollDecay = copy(source.fCasualtyRollDecay);
		target.fInjury = source.fInjury;
		target.fInjuryDecay = source.fInjuryDecay;
		target.fSeriousInjury = source.fSeriousInjury;
		target.fSeriousInjuryDecay = source.fSeriousInjuryDecay;
		target.originalSeriousInjury = source.originalSeriousInjury;
		target.fSendToBoxReason = source.fSendToBoxReason;
		target.fSendToBoxTurn = source.fSendToBoxTurn;
		target.fSendToBoxHalf = source.fSendToBoxHalf;
		target.fSound = source.fSound;
		target.fSufferedInjury = source.fSufferedInjury;
		target.fApothecaryMode = source.fApothecaryMode;
		target.fApothecaryStatus = source.fApothecaryStatus;
		target.casualtyModifiers.addAll(source.casualtyModifiers);
		return target;
	}

	private int[] copy(int[] dice) {
		return dice == null ? null : dice.clone();
	}
}
