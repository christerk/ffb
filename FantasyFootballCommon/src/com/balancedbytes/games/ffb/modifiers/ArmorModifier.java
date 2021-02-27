package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.model.Skill;

import java.util.Optional;

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

	public Optional<Skill> getRegisteredTo() {
		return Optional.ofNullable(registeredTo);
	}

	public void setRegisteredTo(Skill registeredTo) {
		this.registeredTo = registeredTo;
	}
}
