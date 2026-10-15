package com.fumbbl.ffb.modifiers;

public class JumpUpModifier extends RollModifier<JumpUpContext> {

	private final String fName;
	private final int fModifier;
	private final ModifierType type;

	public JumpUpModifier(String fName, int fModifier, ModifierType type) {
		this(fName, fModifier, type, false);
	}

	public JumpUpModifier(String name, int modifier, ModifierType type, boolean optional) {
		super(optional);
		this.fName = name;
		this.fModifier = modifier;
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
		return false;
	}

	@Override
	public String getReportString() {
		return getName();
	}
}
