package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.skill.Skill;

/**
 * 
 * @author Kalimar
 */
public class PickupModifier extends RollModifier<PickupContext> {
	private final String fName, reportString;
	private final int fModifier;
	private final ModifierType type;

	public PickupModifier(String pName, int pModifier, ModifierType type) {
		this(pName, pName, pModifier, type);
	}

	public PickupModifier(String pName, String reportString, int pModifier, ModifierType type) {
		this(pName, reportString, pModifier, type, false);
	}

	public PickupModifier(String name, int modifier, ModifierType type, boolean optional) {
		this(name, name, modifier, type, optional);
	}

	public PickupModifier(String pName, String reportString, int pModifier, ModifierType type, boolean optional) {
		super(optional);
		fName = pName;
		fModifier = pModifier;
		this.type = type;
		this.reportString = reportString;
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

	public boolean appliesToContext(Skill skill, PickupContext context) {
		return true;
	}

	@Override
	public String getReportString() {
		return reportString;
	}

}
