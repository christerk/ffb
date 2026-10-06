package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.skill.Skill;

/**
 *
 * @author Kalimar
 */
public class JumpModifier extends RollModifier<JumpContext> {

	private final String fName, reportString;
	private final int fModifier, multiplier;
	private final ModifierType type;

	public JumpModifier(String pName, int pModifier, ModifierType type) {
		this(pName, pName, pModifier, pModifier, type);
	}

	public JumpModifier(String pName, int pModifier, ModifierType type, boolean optional) {
		this(pName, pName, pModifier, pModifier, type, optional);
	}

	public JumpModifier(String pName, String reportString, int pModifier, int multiplier, ModifierType type) {
		this(pName, reportString, pModifier, multiplier, type, false);
	}

	public JumpModifier(String pName, String reportString, int pModifier, int multiplier, ModifierType type,
		boolean optional) {
		super(optional);
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
		return type == ModifierType.TACKLEZONE || type == ModifierType.PREHENSILE_TAIL;
	}

	@Override
	public String getReportString() {
		return reportString;
	}

	public boolean appliesToContext(Skill skill, JumpContext context) {
		return true;
	}
}
