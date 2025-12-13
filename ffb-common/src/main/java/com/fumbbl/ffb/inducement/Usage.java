package com.fumbbl.ffb.inducement;

import java.util.HashSet;
import java.util.Set;

public enum Usage {
	ADD_LINEMEN, ADD_CHEERLEADER, ADD_COACH, ADD_TO_ARGUE_ROLL, APOTHECARY, APOTHECARY_JOURNEYMEN, AVOID_BAN,
	CHANGE_WEATHER, GAME_MODIFICATION, KNOCKOUT_RECOVERY,
	LONER, REGENERATION, REROLL, REROLL_ARGUE, REROLL_ONES_ON_KOS,
	SPELL, SPOT_FOUL, STAFF, STAR, STEAL_REROLL, THROW_ROCK, UNSPECIFIC;

	public static final Set<Usage> EXCLUDE_FROM_RESULT = new HashSet<Usage>() {{
		add(LONER);
		add(STAR);
		add(STAFF);
		add(REROLL_ARGUE);
		add(REROLL_ONES_ON_KOS);
	}};

	public static final Set<Usage> EXCLUDE_FROM_COUNT = new HashSet<Usage>() {{
		add(REROLL_ARGUE);
		add(REROLL_ONES_ON_KOS);
		add(THROW_ROCK);
	}};
}
