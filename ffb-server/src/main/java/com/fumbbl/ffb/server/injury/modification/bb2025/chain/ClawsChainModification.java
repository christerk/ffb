package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * BB2025 Claws with {@code CLAWS_OPTIONAL}: reduces the armour to 8+ on a block. Skills cancelling Claws make it
 * inapplicable, the stacking rule of {@code CLAW_DOES_NOT_STACK} is applied by {@link InjuryNodeEvaluationService}.
 */
public class ClawsChainModification extends OptionalSkillChainModification {

	public ClawsChainModification() {
		super(Collections.singleton(Block.class));
	}

	@Override
	public Set<InjuryPhase> phases() {
		return EnumSet.of(InjuryPhase.ARMOUR);
	}

	@Override
	public ModificationRestriction restriction() {
		return ModificationRestriction.STAT;
	}

	@Override
	public SkillUse skillUse(InjuryPhase phase) {
		return null;
	}

	@Override
	protected boolean isAllowed(InjuryChainContext context, InjuryNode node, InjuryPhase phase, Player<?> owner,
		Player<?> defender) {
		return isAllowedForBlock(context, node, phase, owner, defender)
			&& defender.getSkillsIncludingTemporaryOnes().stream().noneMatch(skill -> skill.canCancel(getSkill()));
	}
}
