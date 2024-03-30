package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.model.Player;

public interface ArmorModifier extends INamedObject, IRegistrationAwareModifier {

	int getModifier(Player<?> attacker, Player<?> defender);

	String getName();

	boolean isFoulAssistModifier();

	boolean appliesToContext(ArmorModifierContext context);

}
