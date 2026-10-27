package com.fumbbl.ffb.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Player and team keywords. The official keywords are predefined constants, any other name (e.g. from custom league
 * rosters) results in a custom keyword that keeps its original spelling. Keywords are compared case-insensitively by
 * name.
 */
public final class Keyword implements Comparable<Keyword> {

	private static final Map<String, Keyword> OFFICIAL = new HashMap<>();
	private static final List<Keyword> OFFICIAL_ORDER = new ArrayList<>();

	// team level
	public static final Keyword MASTER_CHEF = official("Master Chef");
	public static final Keyword VAMPIRE_LORD = official("Vampire Lord");

	// player level
	public static final Keyword ANIMAL = official("Animal");
	public static final Keyword BEASTMAN = official("Beastman");
	public static final Keyword BIG_GUY = official("Big Guy", false);
	public static final Keyword BLITZER = official("Blitzer", false);
	public static final Keyword BLOCKER = official("Blocker", false);
	public static final Keyword BUGMAN = official("Bugman", false);
	public static final Keyword CATCHER = official("Catcher", false);
	public static final Keyword CONSTRUCT = official("Construct");
	public static final Keyword DWARF = official("Dwarf");
	public static final Keyword ELF = official("Elf");
	public static final Keyword GHOUL = official("Ghoul");
	public static final Keyword GNOBLAR = official("Gnoblar");
	public static final Keyword GNOME = official("Gnome");
	public static final Keyword GOBLIN = official("Goblin");
	public static final Keyword HALFLING = official("Halfling");
	public static final Keyword HUMAN = official("Human");
	public static final Keyword LINEMAN = official("Lineman", false);
	public static final Keyword LIZARDMAN = official("Lizardman");
	public static final Keyword MINOTAUR = official("Minotaur");
	public static final Keyword OGRE = official("Ogre");
	public static final Keyword ORC = official("Orc");
	public static final Keyword RUNNER = official("Runner", false);
	public static final Keyword SKAVEN = official("Skaven");
	public static final Keyword SKELETON = official("Skeleton");
	public static final Keyword SNAKEMAN = official("Snakeman");
	public static final Keyword SNOTLING = official("Snotling");
	public static final Keyword SPAWN = official("Spawn");
	public static final Keyword SPECIAL = official("Special", false);
	public static final Keyword SPITE = official("Spite");
	public static final Keyword SQUIRREL = official("Squirrel");
	public static final Keyword THRALL = official("Thrall");
	public static final Keyword THROWER = official("Thrower", false);
	public static final Keyword TREEMAN = official("Treeman");
	public static final Keyword TROLL = official("Troll");
	public static final Keyword UNDEAD = official("Undead");
	public static final Keyword VAMPIRE = official("Vampire");
	public static final Keyword WEREWOLF = official("Werewolf");
	public static final Keyword WRAITH = official("Wraith");
	public static final Keyword YHETEE = official("Yhetee");
	public static final Keyword ZOAT = official("Zoat");
	public static final Keyword ZOMBIE = official("Zombie");

	// fallback
	public static final Keyword ALL = official("all");
	public static final Keyword UNKNOWN = official("Unknown");

	private final String name;
	private final boolean canGetEvenWith;
	private final boolean custom;

	private Keyword(String name, boolean canGetEvenWith, boolean custom) {
		this.name = name;
		this.canGetEvenWith = canGetEvenWith;
		this.custom = custom;
	}

	private static Keyword official(String name) {
		return official(name, true);
	}

	private static Keyword official(String name, boolean canGetEvenWith) {
		Keyword keyword = new Keyword(name, canGetEvenWith, false);
		OFFICIAL.put(key(name), keyword);
		OFFICIAL_ORDER.add(keyword);
		return keyword;
	}

	private static String key(String name) {
		return name.trim().toLowerCase(Locale.ROOT);
	}

	/**
	 * @return the official keyword matching the name (ignoring case), otherwise a custom keyword keeping the given
	 * spelling. Missing names result in {@link #UNKNOWN}.
	 */
	public static Keyword forName(String name) {
		if (name == null || name.trim().isEmpty()) {
			return UNKNOWN;
		}
		Keyword official = OFFICIAL.get(key(name));
		if (official != null) {
			return official;
		}
		return new Keyword(name.trim(), true, true);
	}

	public boolean isCanGetEvenWith() {
		return canGetEvenWith;
	}

	public boolean isCustom() {
		return custom;
	}

	/**
	 * @return the name as defined by the rules or the roster, to be used for all internal handling
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the name for rendering in the ui, custom keywords get the first letter of each word upper cased
	 */
	public String getDisplayName() {
		if (!custom) {
			return name;
		}
		return Arrays.stream(name.split(" ", -1))
			.map(word -> word.isEmpty() ? word : word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1))
			.collect(Collectors.joining(" "));
	}

	/**
	 * Official keywords first in their defined order, followed by custom keywords sorted by name.
	 */
	@Override
	public int compareTo(Keyword other) {
		if (!custom && !other.custom) {
			return Integer.compare(OFFICIAL_ORDER.indexOf(this), OFFICIAL_ORDER.indexOf(other));
		}
		if (custom != other.custom) {
			return custom ? 1 : -1;
		}
		return key(name).compareTo(key(other.name));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Keyword)) {
			return false;
		}
		return key(name).equals(key(((Keyword) o).name));
	}

	@Override
	public int hashCode() {
		return key(name).hashCode();
	}

	@Override
	public String toString() {
		return name;
	}
}
