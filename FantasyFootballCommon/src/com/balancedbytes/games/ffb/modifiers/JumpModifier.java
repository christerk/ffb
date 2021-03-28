package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.model.skill.Skill;

/**
 *
 * @author Kalimar
 */
public class JumpModifier extends RollModifier<JumpContext> {

	// TODO: create factory for this

	private final String fName;
	private final int fModifier;
	private final ModifierType type;

	public JumpModifier(String pName, int pModifier, ModifierType type) {
		fName = pName;
		fModifier = pModifier;
		this.type = type;
	}

	public int getModifier() {
		return fModifier;
	}

	public String getName() {
		return fName;
	}

	@Override
	public ModifierType getType() {
		return type;
	}

	public boolean isModifierIncluded() {
		return type == ModifierType.TACKLEZONE;
	}

	@Override
	public String getReportString() {
		return getName();
	}

	public boolean appliesToContext(Skill skill, JumpContext context) {
		return true;
	}
}
