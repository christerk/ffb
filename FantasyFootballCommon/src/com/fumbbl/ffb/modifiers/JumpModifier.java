package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.skill.Skill;

/**
 *
 * @author Kalimar
 */
public class JumpModifier extends RollModifier<JumpContext> {

	// TODO: create factory for this

	private final String fName, reportString;
	private final int fModifier, multiplier;
	private final ModifierType type;

	public JumpModifier(String pName, int pModifier, ModifierType type) {
		this(pName, pName, pModifier, pModifier, type);
	}
	public JumpModifier(String pName, String reportString, int pModifier, int multiplier, ModifierType type) {
		fName = pName;
		fModifier = pModifier;
		this.type = type;
		this.reportString = reportString;
		this.multiplier = multiplier;
	}

	public int getModifier() {
		return fModifier;
	}

	@Override
	public int getMultiplier() {
		return multiplier;
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
		return reportString;
	}

	public boolean appliesToContext(Skill skill, JumpContext context) {
		return true;
	}
}
