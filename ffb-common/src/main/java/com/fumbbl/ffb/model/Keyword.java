package com.fumbbl.ffb.model;

import java.util.Arrays;

public enum Keyword {
	VAMPIRE_LORD("Vampire Lord"),
	THRALL("Thrall"),
	DWARF("Dwarf"),
	MASTER_CHEF("master chef"),
	UNKNOWN("unknown"),

	// 2025
	ANIMAL("Animal"),
	BEASTMAN("Beastman"),
	BIG_GUY("Big guy"),
	BLITZER("Blitzer"),
	BLOCKER("Blocker"),
	CATCHER("Catcher"),
	CONSTRUCT("Construct"),
	DRYAD("Dryad"),
	ELF("Elf"),
	GHOUL("Ghoul"),
	GNOME("Gnome"),
	GNOBLAR("Gnoblar"),
	GOBLIN("Goblin"),
	HALFLING("Halfling"),
	HUMAN("Human"),
	LIZARDMAN("Lizardman"),
	LINEMAN("Lineman"),
	MINOTAUR("Minotaur"),
	OGRE("Ogre"),
	ORC("Orc"),
	RUNNER("Runner"),
	SKELETON("Skeleton"),
	SKAVEN("Skaven"),
	SKINK("Skink"),
	SNAKEMAN("Snakeman"),
	SNOTLING("Snotling"),
	SPAWN("Spawn"),
	SPECIAL("Special"),
	SPITE("Spite"),
	SQUIRREL("Squirrel"),
	THROWER("Thrower"),
	TREEMAN("Treeman"),
	TROLL("Troll"),
	UNDEAD("Undead"),
	VAMPIRE("Vampire"),
	WRAITH("Wraith"),
	WEREWOLF("Werewolf"),
	YHETEE("Yhetee"),
	ZOAT("Zoat"),
	ZOMBIE("Zombie");


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
