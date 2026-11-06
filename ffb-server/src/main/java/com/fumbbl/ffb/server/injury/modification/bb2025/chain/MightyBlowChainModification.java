package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.Bomb;
import com.fumbbl.ffb.injury.BombForSpp;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.injury.injuryType.AbstractInjuryTypeBombWithModifier;

import java.util.Arrays;
import java.util.HashSet;

/**
 * BB2025 Mighty Blow with {@code MIGHTY_BLOW_OPTIONAL}: +1 to the armour or the injury roll of a block, for bombs
 * thrown at opponents only to the injury roll as in the base evaluation.
 */
public class MightyBlowChainModification extends OptionalSkillChainModification {

	public MightyBlowChainModification() {
		super(new HashSet<>(Arrays.<Class<? extends InjuryType>>asList(Block.class, Bomb.class, BombForSpp.class)));
	}

	@Override
	protected boolean isAllowed(InjuryChainContext context, InjuryNode node, InjuryPhase phase, Player<?> owner,
		Player<?> defender) {
		if (context.getTypeServer() instanceof AbstractInjuryTypeBombWithModifier) {
			return phase == InjuryPhase.INJURY && node.getContext().fInjuryType.isCausedByOpponent();
		}
		return node.getContext().fInjuryType instanceof Block && isAllowedForBlock(context, node, phase, owner, defender);
	}
}
