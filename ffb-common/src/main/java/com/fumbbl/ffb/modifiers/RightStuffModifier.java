package com.fumbbl.ffb.modifiers;

/**
 * 
 * @author Kalimar
 */
public class RightStuffModifier extends RollModifier<RightStuffContext> {

	private final String fName;
	private final int fModifier;
	private final ModifierType type;

	public RightStuffModifier(String pName, int pModifier, ModifierType type) {
		this(pName, pModifier, type, false);
	}

	public RightStuffModifier(String pName, int pModifier, ModifierType type, boolean optional) {
		super(optional);
		fName = pName;
		fModifier = pModifier;
		this.type = type;
	}

	@Override
	public ModifierType getType() {
		return type;
	}

	public int getModifier() {
		return fModifier;
	}

	public String getName() {
		return fName;
	}

	public boolean isModifierIncluded() {
		return type == ModifierType.TACKLEZONE;
	}

	@Override
	public String getReportString() {
		return getName();
	}

}
