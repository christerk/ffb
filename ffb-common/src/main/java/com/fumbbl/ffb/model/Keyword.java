package com.fumbbl.ffb.model;

import java.util.Arrays;

public enum Keyword {
	// team level
	MASTER_CHEF("Master Chef"),
	VAMPIRE_LORD("Vampire Lord"),

	// player level
	ANIMAL("Animal"),
	BEASTMAN("Beastman"),
	BIG_GUY("Big Guy", false),
	BLITZER("Blitzer", false),
	BLOCKER("Blocker", false),
	BUGMAN("Bugman", false),
	CATCHER("Catcher", false),
	CONSTRUCT("Construct"),
	DWARF("Dwarf"),
	ELF("Elf"),
	GHOUL("Ghoul"),
	GNOBLAR("Gnoblar"),
	GNOME("Gnome"),
	GOBLIN("Goblin"),
	HALFLING("Halfling"),
	HUMAN("Human"),
	LINEMAN("Lineman", false),
	LIZARDMAN("Lizardman"),
	MINOTAUR("Minotaur"),
	OGRE("Ogre"),
	ORC("Orc"),
	RUNNER("Runner", false),
	SKAVEN("Skaven"),
	SKELETON("Skeleton"),
	SNAKEMAN("Snakeman"),
	SNOTLING("Snotling"),
	SPAWN("Spawn"),
	SPECIAL("Special", false),
	SQUIRREL("Squirrel"),
	THRALL("Thrall"),
	THROWER("Thrower", false),
	TREEMAN("Treeman"),
	TROLL("Troll"),
	UNDEAD("Undead"),
	VAMPIRE("Vampire"),
	WEREWOLF("Werewolf"),
	WRAITH("Wraith"),
	YHETEE("Yhetee"),
	ZOMBIE("Zombie"),

	// fallback
	ALL("all"),
	UNKNOWN("Unknown");

	private final String name;
	private final boolean canGetEvenWith;

	Keyword(String name) {
		this(name, true);
	}

	Keyword(String name, boolean canGetEvenWith) {
		this.name = name;
		this.canGetEvenWith = canGetEvenWith;
	}

	public boolean isCanGetEvenWith() {
		return canGetEvenWith;
	}

	public static Keyword forName(String name) {
		return Arrays.stream(values()).filter(keyword -> keyword.name.equalsIgnoreCase(name)).findFirst().orElse(UNKNOWN);
	}

	public String getName() {
		return name;
	}
}
