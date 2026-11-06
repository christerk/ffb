package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Set;

/**
 * BB2025 modification of an injury that can be chained with others. Checks and application are separated: the check
 * is side effect free, the application only changes the child node under construction (dice or skill modifiers), the
 * result is recomputed afterwards by {@link InjuryNodeEvaluationService}.
 * <p>
 * Instances are shared by all players owning the skill, the owner is therefore passed in.
 */
public abstract class ChainableInjuryModification {

	private Skill skill;
	private final Set<Class<? extends InjuryType>> validInjuryTypes;
	private final boolean validForAllInjuryTypes;

	protected ChainableInjuryModification(Set<Class<? extends InjuryType>> validInjuryTypes) {
		this(validInjuryTypes, false);
	}

	protected ChainableInjuryModification(Set<Class<? extends InjuryType>> validInjuryTypes,
		boolean validForAllInjuryTypes) {
		this.validInjuryTypes = validInjuryTypes;
		this.validForAllInjuryTypes = validForAllInjuryTypes;
	}

	public abstract Set<InjuryPhase> phases();

	public abstract ModificationEffect effect();

	public ModificationRestriction restriction() {
		return ModificationRestriction.NONE;
	}

	public OwnerSide ownerSide() {
		return OwnerSide.ATTACKER;
	}

	/**
	 * Side effect free check whether this modification can be applied by the given owner on the node in the phase.
	 * Modifiers only check their preconditions, whether they change the result is decided per combination.
	 */
	public abstract boolean isApplicable(InjuryChainContext context, InjuryNode node, InjuryPhase phase, String ownerId,
		EligibilityMode mode);

	/**
	 * Changes the dice or adds skill modifiers on the child under construction, adds edge reports if required.
	 */
	public abstract void apply(InjuryChainContext context, InjuryNode child, InjuryPhase phase, String ownerId);

	/**
	 * The use reported on acceptance, {@code null} if the new result already shows the use (e.g. Claws).
	 */
	public abstract SkillUse skillUse(InjuryPhase phase);

	public boolean requiresConditionalReRollSkill() {
		return false;
	}

	/**
	 * Whether the attacker may be asked although the armour is already broken.
	 */
	public boolean offeredOnBrokenArmour() {
		return false;
	}

	public boolean isValidType(InjuryType injuryType) {
		return injuryType != null && (validForAllInjuryTypes || validInjuryTypes.contains(injuryType.getClass()));
	}

	public Skill getSkill() {
		return skill;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	/**
	 * In {@link EligibilityMode#BUILD} the player is treated as standing.
	 */
	protected boolean hasTacklezones(Game game, Player<?> player, EligibilityMode mode) {
		if (player == null) {
			return false;
		}
		if (mode == EligibilityMode.BUILD) {
			return true;
		}
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		return playerState != null && playerState.hasTacklezones();
	}

	protected boolean actingPlayerHasTacklezones(Game game, EligibilityMode mode) {
		return hasTacklezones(game, game.getActingPlayer().getPlayer(), mode);
	}

	protected boolean differentTeams(Game game, InjuryContext injuryContext) {
		Player<?> attacker = game.getPlayerById(injuryContext.fAttackerId);
		Player<?> defender = game.getPlayerById(injuryContext.fDefenderId);
		return attacker == null || defender == null || attacker.getTeam() != defender.getTeam();
	}

	protected boolean armourModifiersBlocked(InjuryChainContext context, InjuryNode node) {
		Player<?> defender = context.getDefender(node);
		return defender != null
			&& UtilCards.hasUnusedSkillWithProperty(defender, NamedProperties.ignoresArmourModifiersFromSkills);
	}

	/**
	 * The evaluated context of the node the child under construction is created from.
	 */
	protected InjuryContext parentContext(InjuryChainContext context, InjuryNode child) {
		return context.getTree().getNode(child.getParentId()).getContext();
	}

	protected boolean isArmourRolled(InjuryNode node) {
		return node.getContext().getArmorRoll() != null;
	}
}
