package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.server.GameState;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Adds the skill's modifiers either to the armour or to the injury roll (Ram, Dwarven Scourge, Slayer).
 */
public class AvOrInjChainModification extends ChainableInjuryModification {

	private final ModificationRestriction restriction;

	public AvOrInjChainModification() {
		this(new NoRestriction());
	}

	public AvOrInjChainModification(ModificationRestriction restriction) {
		this(restriction, new InjuryEvaluationService(), new InjuryContextCopyService());
	}

	AvOrInjChainModification(ModificationRestriction restriction, InjuryEvaluationService evaluationService,
		InjuryContextCopyService copyService) {
		super(Collections.<Class<? extends InjuryType>>singleton(Block.class), false, evaluationService, copyService);
		this.restriction = restriction;
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
	public SkillUse skillUse(InjuryPhase phase) {
		return phase == InjuryPhase.INJURY ? SkillUse.ADD_INJURY_MODIFIER : SkillUse.ADD_ARMOUR_MODIFIER;
	}

	@Override
	public ModificationRestriction restriction() {
		return restriction;
	}

	@Override
	protected boolean isApplicableToArmour(GameState gameState, InjuryResolutionState state) {
		InjuryContext injuryContext = state.getInjuryContext();
		return !injuryContext.isArmorBroken()
			&& actingPlayerHasTacklezones(gameState.getGame())
			&& breaksArmourWith(gameState, injuryContext, skillArmourModifiers());
	}

	@Override
	protected void applyToArmour(GameState gameState, InjuryResolutionState state, List<IReport> reports) {
		addArmourModifiers(gameState, state, skillArmourModifiers());
	}

	@Override
	protected boolean isApplicableToInjury(GameState gameState, InjuryResolutionState state) {
		InjuryContext injuryContext = state.getInjuryContext();
		return !isCasualty(injuryContext)
			&& actingPlayerHasTacklezones(gameState.getGame())
			&& Collections.disjoint(Arrays.asList(injuryContext.getArmorModifiers()), skillArmourModifiers())
			&& changesInjuryWith(gameState, injuryContext, skillInjuryModifiers());
	}

	@Override
	protected void applyToInjury(GameState gameState, InjuryResolutionState state, List<IReport> reports) {
		addInjuryModifiers(gameState, state, skillInjuryModifiers());
	}
}
