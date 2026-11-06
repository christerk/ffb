package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.injury.DropDodgeForSpp;
import com.fumbbl.ffb.injury.DropJump;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.modifiers.InjuryModifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * BB2025 Arm Bar with {@code ARM_BAR_OPTIONAL}: +1 to the armour or the injury roll of a player falling after a
 * failed dodge or jump. The modifiers never apply via their context, the eligible player is determined by the
 * injury type.
 */
public class ArmBarChainModification extends OptionalSkillChainModification {

	public ArmBarChainModification() {
		super(new HashSet<>(Arrays.<Class<? extends InjuryType>>asList(DropDodgeForSpp.class, DropJump.class)));
	}

	@Override
	protected boolean isAllowed(InjuryChainContext context, InjuryNode node, InjuryPhase phase, Player<?> owner,
		Player<?> defender) {
		return context.getTypeServer().optionalSkillAttackers(context.getGame(), defender, context.getFromCoordinate())
			.contains(owner);
	}

	@Override
	protected List<ArmorModifier> armourModifiers(InjuryChainContext context, InjuryNode node, Player<?> owner,
		Player<?> defender) {
		return new ArrayList<>(getSkill().getArmorModifiers());
	}

	@Override
	protected List<InjuryModifier> injuryModifiers(InjuryChainContext context, InjuryNode node, Player<?> owner,
		Player<?> defender) {
		return new ArrayList<>(getSkill().getInjuryModifiers());
	}
}
