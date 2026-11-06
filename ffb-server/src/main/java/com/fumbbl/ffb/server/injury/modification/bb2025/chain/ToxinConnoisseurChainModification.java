package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.Stab;
import com.fumbbl.ffb.injury.StabForSpp;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class ToxinConnoisseurChainModification extends ChainableInjuryModification {

	public ToxinConnoisseurChainModification() {
		super(validTypes());
	}

	private static Set<Class<? extends InjuryType>> validTypes() {
		Set<Class<? extends InjuryType>> types = new HashSet<>();
		types.add(Stab.class);
		types.add(StabForSpp.class);
		return types;
	}

	@Override
	public Set<InjuryPhase> phases() {
		return EnumSet.of(InjuryPhase.INJURY);
	}

	@Override
	public ModificationEffect effect() {
		return ModificationEffect.MODIFIER;
	}

	@Override
	public boolean isApplicable(InjuryChainContext context, InjuryNode node, InjuryPhase phase, String ownerId,
		EligibilityMode mode) {
		return node.getContext().isArmorBroken() && !node.getContext().isCasualty()
			&& differentTeams(context.getGame(), node.getContext());
	}

	@Override
	public void apply(InjuryChainContext context, InjuryNode child, InjuryPhase phase, String ownerId) {
		child.getSkillInjuryModifiers().addAll(getSkill().getInjuryModifiers());
	}

	@Override
	public SkillUse skillUse(InjuryPhase phase) {
		return SkillUse.ADD_INJURY_MODIFIER;
	}
}
