package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.injury.FoulForSpp;
import com.fumbbl.ffb.injury.FoulForSppWithChainsaw;
import com.fumbbl.ffb.injury.FoulWithChainsaw;
import com.fumbbl.ffb.injury.InjuryType;

import java.util.Arrays;
import java.util.HashSet;

/**
 * BB2025 Dirty Player with {@code DIRTY_PLAYER_OPTIONAL}: +1 to the armour or the injury roll of a foul.
 */
public class DirtyPlayerChainModification extends OptionalSkillChainModification {

	public DirtyPlayerChainModification() {
		super(new HashSet<>(Arrays.<Class<? extends InjuryType>>asList(Foul.class, FoulForSpp.class,
			FoulWithChainsaw.class, FoulForSppWithChainsaw.class)));
	}
}
