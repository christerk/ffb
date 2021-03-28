package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.model.Player;

public interface InjuryModifier extends INamedObject, IRegistrationAwareModifier {

	int getModifier(Player<?> attacker, Player<?> defender);

	String getName();

	boolean isNigglingInjuryModifier();

	boolean appliesToContext(InjuryModifierContext context);

}
