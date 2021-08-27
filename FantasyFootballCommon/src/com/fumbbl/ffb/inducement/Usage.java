package com.fumbbl.ffb.inducement;

import java.util.HashSet;
import java.util.Set;

public enum Usage {
	ADD_LINEMEN, APOTHECARY, AVOID_BAN, GAME_MODIFICATION, KNOCKOUT_RECOVERY, LONER, REGENERATION, REROLL,
	REROLL_ARGUE, SPELL, STAR, STEAL_REROLL, UNSPECIFIC;

	public static Set<Usage> REQUIRE_EXPLICIT_SELECTION = new HashSet<Usage>() {{
		add(LONER);
		add(STAR);
		add(GAME_MODIFICATION);
	}};

	public static Set<Usage> EXCLUDE_FROM_RESULT = new HashSet<Usage>() {{
		add(LONER);
		add(STAR);
	}};

}
