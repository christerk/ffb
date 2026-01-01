package com.fumbbl.ffb.model;

import java.util.Arrays;

public enum Keyword {
	// team level
	MASTER_CHEF("Master Chef"),
	VAMPIRE_LORD("Vampire Lord"),

	// player level
	ANIMAL("Animal"),
	BEASTMAN("Beastman"),
	BIG_GUY("Big Guy"),
	BLITZER("Blitzer"),
	BLOCKER("Blocker"),
	CATCHER("Catcher"),
	CONSTRUCT("Construct"),
	DWARF("Dwarf"),
	ELF("Elf"),
	GHOUL("Ghoul"),
	GNOBLAR("Gnoblar"),
	GNOME("Gnome"),
	GOBLIN("Goblin"),
	HALFLING("Halfling"),
	HUMAN("Human"),
	LINEMAN("Lineman"),
	LIZARDMAN("Lizardman"),
	MINOTAUR("Minotaur"),
	OGRE("Ogre"),
	ORC("Orc"),
	RUNNER("Runner"),
	SKAVEN("Skaven"),
	SKELETON("Skeleton"),
	SNOTLING("Snotling"),
	SPAWN("Spawn"),
	SPECIAL("Special"),
	THRALL("Thrall"),
	THROWER("Thrower"),
	TREEMAN("Treeman"),
	TROLL("Troll"),
	UNDEAD("Undead"),
	VAMPIRE("Vampire"),
	WEREWOLF("Werewolf"),
	WRAITH("Wraith"),
	ZOMBIE("Zombie"),

	// fallback
	UNKNOWN("Unknown");

	private final String name;

	Keyword(String name) {
		this.name = name;
	}

	public static Keyword forName(String name) {
		return Arrays.stream(values()).filter(keyword -> keyword.name.equalsIgnoreCase(name)).findFirst().orElse(UNKNOWN);
	}

	public String getName() {
		return name;
	}
}
