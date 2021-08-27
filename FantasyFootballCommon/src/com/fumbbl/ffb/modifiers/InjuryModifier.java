package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.model.Player;

public interface InjuryModifier extends INamedObject, IRegistrationAwareModifier {

	int getModifier(Player<?> attacker, Player<?> defender);

	String getName();

	boolean isNigglingInjuryModifier();

	boolean appliesToContext(InjuryModifierContext context);

}
