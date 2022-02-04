package com.fumbbl.ffb;

/**
 * LRB5 Skill Categories
 *
 * @author Kalimar
 */
public enum SkillCategory implements INamedObject {

	GENERAL("General"), AGILITY("Agility"), PASSING("Passing"), STRENGTH("Strength"),
	MUTATION("Mutation", "Mutations"), MUTATIONS("Mutations"), EXTRAORDINARY("Extraordinary"), STAT_INCREASE("Stat Increase"),
	STAT_DECREASE("Stat Decrease"), TRAIT("Trait");

	private final String fName;
	private final String altName;

	SkillCategory(String pName) {
		this(pName, pName);
	}

	SkillCategory(String pName, String altName) {
		fName = pName;
		this.altName = altName;
	}

	public String getName() {
		return fName;
	}

	public String getAltName() {
		return altName;
	}
}
