package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.InjuryType;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Re-rolls both armour dice. By default offered to the attacker when the armour holds.
 */
public abstract class ArmourReRollChainModification extends ChainableInjuryModification {

	protected ArmourReRollChainModification(Set<Class<? extends InjuryType>> validInjuryTypes) {
		super(validInjuryTypes);
	}

	protected ArmourReRollChainModification(boolean validForAllInjuryTypes) {
		super(Collections.<Class<? extends InjuryType>>emptySet(), validForAllInjuryTypes);
	}

	@Override
	public Set<InjuryPhase> phases() {
		return EnumSet.of(InjuryPhase.ARMOUR);
	}

	@Override
	public ModificationEffect effect() {
		return ModificationEffect.RE_ROLL;
	}

	@Override
	public boolean isApplicable(InjuryChainContext context, InjuryNode node, InjuryPhase phase, String ownerId,
		EligibilityMode mode) {
		return isArmourRolled(node) && !node.getContext().isArmorBroken()
			&& differentTeams(context.getGame(), node.getContext());
	}

	@Override
	public void apply(InjuryChainContext context, InjuryNode child, InjuryPhase phase, String ownerId) {
		child.setArmourDice(context.getTree().getPool().armourReRoll(context.getGameState().getDiceRoller()));
		child.setArmourReRolled(true);
	}

	@Override
	public SkillUse skillUse(InjuryPhase phase) {
		return SkillUse.RE_ROLL_ARMOUR;
	}
}
