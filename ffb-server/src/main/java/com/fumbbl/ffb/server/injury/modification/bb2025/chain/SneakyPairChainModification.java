package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.injury.FoulForSpp;
import com.fumbbl.ffb.injury.FoulForSppWithChainsaw;
import com.fumbbl.ffb.injury.FoulWithChainsaw;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.Stab;
import com.fumbbl.ffb.injury.StabForSpp;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeStab;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeStabForSpp;

import java.util.Arrays;
import java.util.HashSet;

/**
 * BB2025 A Sneaky Pair with {@code SNEAKY_PAIR_OPTIONAL}: +1 to the armour or the injury roll of a foul or stab if
 * the partner marks the defender.
 */
public class SneakyPairChainModification extends OptionalSkillChainModification {

	public SneakyPairChainModification() {
		super(new HashSet<>(Arrays.<Class<? extends InjuryType>>asList(Foul.class, FoulForSpp.class,
			FoulWithChainsaw.class, FoulForSppWithChainsaw.class, Stab.class, StabForSpp.class)));
	}

	@Override
	protected boolean isAllowed(InjuryChainContext context, InjuryNode node, InjuryPhase phase, Player<?> owner,
		Player<?> defender) {
		if (phase != InjuryPhase.INJURY) {
			return true;
		}
		if (context.getTypeServer() instanceof InjuryTypeStab) {
			return ((InjuryTypeStab) context.getTypeServer()).isUseInjuryModifiers();
		}
		if (context.getTypeServer() instanceof InjuryTypeStabForSpp) {
			return ((InjuryTypeStabForSpp) context.getTypeServer()).isUseInjuryModifiers();
		}
		return true;
	}
}
