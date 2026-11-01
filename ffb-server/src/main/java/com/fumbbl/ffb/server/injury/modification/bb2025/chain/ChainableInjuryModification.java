package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.modifiers.InjuryModifier;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * BB2025 armour / injury modification that can be part of a chain of modifications.
 * <p>
 * Checks ({@link #isApplicable(GameState, InjuryResolutionState)}) are side effect free, dice are only rolled in
 * {@link #apply(GameState, InjuryResolutionState)}.
 */
public abstract class ChainableInjuryModification {

	private static final ModificationRestriction NO_RESTRICTION = new NoRestriction();

	private final Set<Class<? extends InjuryType>> validInjuryTypes;
	private final boolean validForAllInjuryTypes;
	protected final InjuryEvaluationService evaluationService;
	protected final InjuryContextCopyService copyService;
	private Skill skill;

	protected ChainableInjuryModification(Set<Class<? extends InjuryType>> validInjuryTypes,
		boolean validForAllInjuryTypes, InjuryEvaluationService evaluationService, InjuryContextCopyService copyService) {
		this.validInjuryTypes = validInjuryTypes;
		this.validForAllInjuryTypes = validForAllInjuryTypes;
		this.evaluationService = evaluationService;
		this.copyService = copyService;
	}

	public abstract Set<InjuryPhase> phases();

	public abstract ModificationEffect effect();

	public abstract SkillUse skillUse(InjuryPhase phase);

	public ModificationRestriction restriction() {
		return NO_RESTRICTION;
	}

	public boolean requiresConditionalReRollSkill() {
		return false;
	}

	/**
	 * Allows the owner to be asked although the armour is already broken and the owner is not the defender.
	 */
	public boolean offeredOnBrokenArmour() {
		return false;
	}

	protected boolean ownedByDefender() {
		return false;
	}

	/**
	 * @return id of the player who has to own the skill for this modification, <code>null</code> if nobody can use it
	 */
	public String ownerId(InjuryContext injuryContext) {
		if (ownedByDefender()) {
			return injuryContext.getDefenderId();
		}
		if (StringTool.isProvided(injuryContext.getAttackerId())) {
			return injuryContext.getAttackerId();
		}
		InjuryType injuryType = injuryContext.getInjuryType();
		if (injuryType != null && (injuryType.isChainsaw() || injuryType.isVomitLike())) {
			return injuryContext.getDefenderId();
		}
		return null;
	}

	public boolean isValidType(InjuryType injuryType) {
		return injuryType != null && (validForAllInjuryTypes || validInjuryTypes.contains(injuryType.getClass()));
	}

	public final boolean isApplicable(GameState gameState, InjuryResolutionState state) {
		InjuryPhase phase = state.getPhase();
		InjuryContext injuryContext = state.getInjuryContext();
		Game game = gameState.getGame();

		if (!phases().contains(phase) || !isValidType(injuryContext.getInjuryType())) {
			return false;
		}
		if (effect() == ModificationEffect.RE_ROLL && state.isReRolled(phase)) {
			return false;
		}
		String ownerId = ownerId(injuryContext);
		if (ownerId == null || state.isApplied(skill, ownerId)) {
			return false;
		}
		Player<?> defender = game.getPlayerById(injuryContext.getDefenderId());
		if (defender == null || !restriction().isMet(defender)
			|| !allowedForAttackerAndDefenderTeams(game, injuryContext)) {
			return false;
		}
		switch (phase) {
			case ARMOUR:
				return injuryContext.getArmorRoll() != null
					&& !UtilCards.hasUnusedSkillWithProperty(defender, NamedProperties.ignoresArmourModifiersFromSkills)
					&& isApplicableToArmour(gameState, state);
			case INJURY:
				return injuryContext.getInjuryRoll() != null && isApplicableToInjury(gameState, state);
			default:
				return false;
		}
	}

	public final AppliedInjuryModification apply(GameState gameState, InjuryResolutionState state) {
		InjuryPhase phase = state.getPhase();
		List<IReport> reports = new ArrayList<>();
		switch (phase) {
			case ARMOUR:
				applyToArmour(gameState, state, reports);
				break;
			case INJURY:
				applyToInjury(gameState, state, reports);
				break;
			default:
				throw new IllegalStateException("Modification " + getClass().getSimpleName() + " can not be applied in phase " + phase);
		}
		AppliedInjuryModification applied = new AppliedInjuryModification(skill, ownerId(state.getInjuryContext()),
			phase, effect(), skillUse(phase), reports);
		state.addAppliedModification(applied);
		return applied;
	}

	protected boolean isApplicableToArmour(GameState gameState, InjuryResolutionState state) {
		return false;
	}

	protected void applyToArmour(GameState gameState, InjuryResolutionState state, List<IReport> reports) {
		throw new UnsupportedOperationException(getClass().getSimpleName() + " does not modify armour");
	}

	protected boolean isApplicableToInjury(GameState gameState, InjuryResolutionState state) {
		return false;
	}

	protected void applyToInjury(GameState gameState, InjuryResolutionState state, List<IReport> reports) {
		throw new UnsupportedOperationException(getClass().getSimpleName() + " does not modify injury");
	}

	protected boolean allowedForAttackerAndDefenderTeams(Game game, InjuryContext injuryContext) {
		return differentTeams(game, injuryContext);
	}

	protected boolean differentTeams(Game game, InjuryContext injuryContext) {
		Player<?> attacker = game.getPlayerById(injuryContext.getAttackerId());
		Player<?> defender = game.getPlayerById(injuryContext.getDefenderId());
		return attacker == null || defender == null || attacker.getTeam() != defender.getTeam();
	}

	protected boolean actingPlayerHasTacklezones(Game game) {
		ActingPlayer actingPlayer = game.getActingPlayer();
		Player<?> player = actingPlayer != null ? actingPlayer.getPlayer() : null;
		if (player == null) {
			return false;
		}
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		return playerState != null && playerState.hasTacklezones();
	}

	/**
	 * A missing injury after an injury roll means a casualty that still requires the casualty roll.
	 */
	protected boolean isCasualty(InjuryContext injuryContext) {
		return injuryContext.isCasualty() || (injuryContext.getInjuryRoll() != null && injuryContext.getInjury() == null);
	}

	protected boolean breaksArmourWith(GameState gameState, InjuryContext injuryContext,
		Collection<ArmorModifier> modifiers) {
		InjuryContext simulation = copyService.copy(injuryContext);
		simulation.addArmorModifiers(modifiers);
		return evaluationService.isArmourBroken(gameState, simulation);
	}

	protected void addArmourModifiers(GameState gameState, InjuryResolutionState state,
		Collection<ArmorModifier> modifiers) {
		InjuryContext injuryContext = state.getInjuryContext();
		injuryContext.addArmorModifiers(modifiers);
		state.addSkillArmourModifiers(modifiers);
		injuryContext.setArmorBroken(evaluationService.isArmourBroken(gameState, injuryContext));
	}

	protected boolean changesInjuryWith(GameState gameState, InjuryContext injuryContext,
		Collection<InjuryModifier> modifiers) {
		InjuryContext simulation = copyService.copy(injuryContext);
		simulation.addInjuryModifiers(modifiers);
		return !Objects.equals(evaluationService.interpretInjury(gameState, simulation), injuryContext.getInjury());
	}

	protected void addInjuryModifiers(GameState gameState, InjuryResolutionState state,
		Collection<InjuryModifier> modifiers) {
		InjuryContext injuryContext = state.getInjuryContext();
		injuryContext.addInjuryModifiers(modifiers);
		state.addSkillInjuryModifiers(modifiers);
		injuryContext.setInjury(evaluationService.interpretInjury(gameState, injuryContext));
	}

	protected Collection<ArmorModifier> skillArmourModifiers() {
		return skill != null ? skill.getArmorModifiers() : Collections.<ArmorModifier>emptyList();
	}

	protected Collection<InjuryModifier> skillInjuryModifiers() {
		return skill != null ? skill.getInjuryModifiers() : Collections.<InjuryModifier>emptyList();
	}

	public Skill getSkill() {
		return skill;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
	}
}
