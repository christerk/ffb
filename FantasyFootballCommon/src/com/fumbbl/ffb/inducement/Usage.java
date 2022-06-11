package com.fumbbl.ffb.inducement;

import java.util.HashSet;
import java.util.Set;

public enum Usage {
	ADD_LINEMEN, ADD_CHEERLEADER, APOTHECARY, AVOID_BAN, GAME_MODIFICATION, KNOCKOUT_RECOVERY,
	LONER, REGENERATION, REROLL, REROLL_ARGUE, SPELL, STAR, STEAL_REROLL, UNSPECIFIC;

	public static final Set<Usage> REQUIRE_EXPLICIT_SELECTION = new HashSet<Usage>() {{
		add(LONER);
		add(STAR);
		add(GAME_MODIFICATION);
	}};

	public static final Set<Usage> EXCLUDE_FROM_RESULT = new HashSet<Usage>() {{
		add(LONER);
		add(STAR);
		add(REROLL_ARGUE);
	}};

	public static final Set<Usage> EXCLUDE_FROM_COUNT = new HashSet<Usage>() {{
		add(REROLL_ARGUE);
	}};
}
