package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.model.Player;

public interface ArmorModifier extends INamedObject, IRegistrationAwareModifier {

	int getModifier(Player<?> player);

	String getName();

	boolean isFoulAssistModifier();

	boolean appliesToContext(ArmorModifierContext context);

}
