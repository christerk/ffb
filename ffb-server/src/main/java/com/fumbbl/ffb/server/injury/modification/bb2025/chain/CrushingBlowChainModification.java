package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.ArmorModifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adds its armour modifier. On armour already broken with Mighty Blow it replaces Mighty Blow on the armour roll, so
 * Mighty Blow can be used on the injury roll.
 */
public class CrushingBlowChainModification extends ChainableInjuryModification {

	private final OptionalSkillModifierService optionalSkillModifierService = new OptionalSkillModifierService();

	public CrushingBlowChainModification() {
		super(Collections.singleton(Block.class));
	}

	@Override
	public Set<InjuryPhase> phases() {
		return EnumSet.of(InjuryPhase.ARMOUR);
	}

	@Override
	public ModificationEffect effect() {
		return ModificationEffect.MODIFIER;
	}

	@Override
	public boolean offeredOnBrokenArmour() {
		return true;
	}

	@Override
	public boolean isApplicable(InjuryChainContext context, InjuryNode node, InjuryPhase phase, String ownerId,
		EligibilityMode mode) {
		Game game = context.getGame();
		InjuryContext injuryContext = node.getContext();
		return isArmourRolled(node)
			&& (!injuryContext.isArmorBroken() || !mightyBlowModifiers(game, injuryContext).isEmpty())
			&& actingPlayerHasTacklezones(game, mode)
			&& !armourModifiersBlocked(context, node)
			&& differentTeams(game, injuryContext);
	}

	@Override
	public void apply(InjuryChainContext context, InjuryNode child, InjuryPhase phase, String ownerId) {
		InjuryContext parent = parentContext(context, child);
		if (parent.isArmorBroken()) {
			child.getRemovedArmourModifiers().addAll(mightyBlowModifiers(context.getGame(), parent));
		}
		child.getSkillArmourModifiers().addAll(getSkill().getArmorModifiers());
	}

	/**
	 * Mighty Blow of the base evaluation, moved to the injury roll. An optional Mighty Blow is chosen by the coach
	 * instead.
	 */
	private List<ArmorModifier> mightyBlowModifiers(Game game, InjuryContext injuryContext) {
		return Arrays.stream(injuryContext.getArmorModifiers())
			.filter(modifier -> modifier.isRegisteredToSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnBlock))
			.filter(modifier -> !optionalSkillModifierService.isOptional(game, modifier))
			.collect(Collectors.toList());
	}

	@Override
	public SkillUse skillUse(InjuryPhase phase) {
		return SkillUse.ADD_ARMOUR_MODIFIER;
	}
}
