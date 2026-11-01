package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.server.GameState;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adds the Crushing Blow armour modifier. If Mighty Blow already broke the armour, Mighty Blow is removed from the
 * armour (and so can be used for the injury) as long as Crushing Blow alone still breaks the armour.
 */
public class CrushingBlowChainModification extends ChainableInjuryModification {

	public CrushingBlowChainModification() {
		this(new InjuryEvaluationService(), new InjuryContextCopyService());
	}

	CrushingBlowChainModification(InjuryEvaluationService evaluationService, InjuryContextCopyService copyService) {
		super(Collections.<Class<? extends InjuryType>>singleton(Block.class), false, evaluationService, copyService);
	}

	@Override
	public Set<InjuryPhase> phases() {
		return Collections.singleton(InjuryPhase.ARMOUR);
	}

	@Override
	public ModificationEffect effect() {
		return ModificationEffect.MODIFIER;
	}

	@Override
	public SkillUse skillUse(InjuryPhase phase) {
		return SkillUse.ADD_ARMOUR_MODIFIER;
	}

	@Override
	public boolean offeredOnBrokenArmour() {
		return true;
	}

	@Override
	protected boolean isApplicableToArmour(GameState gameState, InjuryResolutionState state) {
		InjuryContext injuryContext = state.getInjuryContext();
		if (!actingPlayerHasTacklezones(gameState.getGame())
			|| (injuryContext.isArmorBroken() && !hasMightyBlowOnArmour(injuryContext))) {
			return false;
		}
		InjuryContext simulation = copyService.copy(injuryContext);
		if (simulation.isArmorBroken()) {
			removeMightyBlowFromArmour(simulation);
		}
		return breaksArmourWith(gameState, simulation, skillArmourModifiers());
	}

	@Override
	protected void applyToArmour(GameState gameState, InjuryResolutionState state, List<IReport> reports) {
		InjuryContext injuryContext = state.getInjuryContext();
		if (injuryContext.isArmorBroken()) {
			removeMightyBlowFromArmour(injuryContext);
		}
		addArmourModifiers(gameState, state, skillArmourModifiers());
	}

	private boolean hasMightyBlowOnArmour(InjuryContext injuryContext) {
		return Arrays.stream(injuryContext.getArmorModifiers()).anyMatch(this::isMightyBlow);
	}

	private void removeMightyBlowFromArmour(InjuryContext injuryContext) {
		Set<ArmorModifier> remaining = Arrays.stream(injuryContext.getArmorModifiers())
			.filter(modifier -> !isMightyBlow(modifier))
			.collect(Collectors.toSet());
		injuryContext.clearArmorModifiers();
		injuryContext.addArmorModifiers(remaining);
	}

	private boolean isMightyBlow(ArmorModifier modifier) {
		return modifier.isRegisteredToSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnBlock);
	}
}
