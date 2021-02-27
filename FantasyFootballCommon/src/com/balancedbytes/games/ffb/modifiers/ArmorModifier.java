package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.model.ISkillProperty;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * 
 * @author Kalimar
 */
public class ArmorModifier implements INamedObject {

	private final String fName;
	private final int fModifier;
	private final boolean fFoulAssistModifier;
	private Skill registeredTo;

	public ArmorModifier(String pName, int pModifier, boolean pFoulAssistModifier) {
		fName = pName;
		fModifier = pModifier;
		fFoulAssistModifier = pFoulAssistModifier;
	}

	public int getModifier() {
		return fModifier;
	}

	public String getName() {
		return fName;
	}

	public boolean isFoulAssistModifier() {
		return fFoulAssistModifier;
	}

	public boolean appliesToContext(ArmorModifierContext context) {
		return true;
	}

	public void setRegisteredTo(Skill registeredTo) {
		this.registeredTo = registeredTo;
	}

	public boolean isRegisteredToSkillWithProperty(ISkillProperty property) {
		return registeredTo != null && registeredTo.hasSkillProperty(property);
	}
}
