package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Adds its modifier either to the armour or to the injury roll of a block.
 */
public class AvOrInjChainModification extends ChainableInjuryModification {

	public AvOrInjChainModification() {
		super(Collections.singleton(Block.class));
	}

	@Override
	public Set<InjuryPhase> phases() {
		return EnumSet.of(InjuryPhase.ARMOUR, InjuryPhase.INJURY);
	}

	@Override
	public ModificationEffect effect() {
		return ModificationEffect.MODIFIER;
	}

	@Override
	public boolean isApplicable(InjuryChainContext context, InjuryNode node, InjuryPhase phase, String ownerId,
		EligibilityMode mode) {
		Game game = context.getGame();
		InjuryContext injuryContext = node.getContext();
		if (!differentTeams(game, injuryContext) || !actingPlayerHasTacklezones(game, mode)) {
			return false;
		}
		if (phase == InjuryPhase.ARMOUR) {
			return isArmourRolled(node) && !injuryContext.isArmorBroken() && !armourModifiersBlocked(context, node);
		}
		return injuryContext.isArmorBroken() && !injuryContext.isCasualty();
	}

	@Override
	public void apply(InjuryChainContext context, InjuryNode child, InjuryPhase phase, String ownerId) {
		if (phase == InjuryPhase.ARMOUR) {
			child.getSkillArmourModifiers().addAll(getSkill().getArmorModifiers());
		} else {
			child.getSkillInjuryModifiers().addAll(getSkill().getInjuryModifiers());
		}
	}

	@Override
	public SkillUse skillUse(InjuryPhase phase) {
		return phase == InjuryPhase.ARMOUR ? SkillUse.ADD_ARMOUR_MODIFIER : SkillUse.ADD_INJURY_MODIFIER;
	}
}
