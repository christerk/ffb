package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.INamedObject;

public interface InjuryModifier extends INamedObject, IRegistrationAwareModifier {

	int getModifier();

	String getName();

	boolean isNigglingInjuryModifier();

	boolean appliesToContext(InjuryModifierContext context);

}
