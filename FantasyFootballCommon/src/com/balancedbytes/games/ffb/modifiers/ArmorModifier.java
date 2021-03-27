package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.INamedObject;

public interface ArmorModifier extends INamedObject, IRegistrationAwareModifier {

	int getModifier();

	String getName();

	boolean isFoulAssistModifier();

	boolean appliesToContext(ArmorModifierContext context);

}
