package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.modifiers.ArmorModifierContext;
import com.fumbbl.ffb.modifiers.InjuryModifier;
import com.fumbbl.ffb.modifiers.InjuryModifierContext;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeBlock;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * BB2025: a regular skill that is applied automatically unless its game option makes it optional. With the option
 * enabled its modifiers are excluded from the base evaluation and offered here, applicability reuses the skill's
 * registered modifiers.
 */
public abstract class OptionalSkillChainModification extends ChainableInjuryModification {

	private final OptionalSkillModifierService optionalSkillModifierService = new OptionalSkillModifierService();

	protected OptionalSkillChainModification(Set<Class<? extends InjuryType>> validInjuryTypes) {
		super(validInjuryTypes);
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
		Player<?> owner = game.getPlayerById(ownerId);
		Player<?> defender = context.getDefender(node);
		if (owner == null || defender == null || !optionalSkillModifierService.isOptional(game, getSkill())
			|| !isAllowed(context, node, phase, owner, defender)) {
			return false;
		}
		InjuryContext injuryContext = node.getContext();
		if (phase == InjuryPhase.ARMOUR) {
			return isArmourRolled(node) && !injuryContext.isArmorBroken() && !armourModifiersBlocked(context, node)
				&& !armourModifiers(context, node, owner, defender).isEmpty();
		}
		return injuryContext.isArmorBroken() && !injuryModifiers(context, node, owner, defender).isEmpty();
	}

	@Override
	public void apply(InjuryChainContext context, InjuryNode child, InjuryPhase phase, String ownerId) {
		Game game = context.getGame();
		InjuryNode parent = context.getTree().getNode(child.getParentId());
		Player<?> owner = game.getPlayerById(ownerId);
		Player<?> defender = context.getDefender(parent);
		if (phase == InjuryPhase.ARMOUR) {
			child.getSkillArmourModifiers().addAll(armourModifiers(context, parent, owner, defender));
		} else {
			child.getSkillInjuryModifiers().addAll(injuryModifiers(context, parent, owner, defender));
		}
	}

	@Override
	public SkillUse skillUse(InjuryPhase phase) {
		return phase == InjuryPhase.ARMOUR ? SkillUse.ADD_ARMOUR_MODIFIER : SkillUse.ADD_INJURY_MODIFIER;
	}

	/**
	 * Restrictions of the injury type beyond the checks of the registered modifiers.
	 */
	protected boolean isAllowed(InjuryChainContext context, InjuryNode node, InjuryPhase phase, Player<?> owner,
		Player<?> defender) {
		return true;
	}

	protected boolean isTtm() {
		return false;
	}

	protected List<ArmorModifier> armourModifiers(InjuryChainContext context, InjuryNode node, Player<?> owner,
		Player<?> defender) {
		InjuryType injuryType = node.getContext().fInjuryType;
		ArmorModifierContext modifierContext = new ArmorModifierContext(context.getGame(), owner, defender,
			injuryType.isStab(), injuryType.isFoul(), 0, isTtm());
		return getSkill().getArmorModifiers().stream()
			.filter(modifier -> modifier.appliesToContext(modifierContext))
			.collect(Collectors.toList());
	}

	protected List<InjuryModifier> injuryModifiers(InjuryChainContext context, InjuryNode node, Player<?> owner,
		Player<?> defender) {
		InjuryType injuryType = node.getContext().fInjuryType;
		InjuryModifierContext modifierContext = new InjuryModifierContext(context.getGame(), node.getContext(), owner,
			defender, injuryType.isStab(), injuryType.isFoul(), injuryType.isVomitLike(), injuryType.isChainsaw(), isTtm());
		return getSkill().getInjuryModifiers().stream()
			.filter(modifier -> modifier.appliesToContext(modifierContext))
			.collect(Collectors.toList());
	}

	/**
	 * Mode and chainsaw rules of {@link InjuryTypeBlock} for the attacker's skills.
	 */
	protected boolean isAllowedForBlock(InjuryChainContext context, InjuryNode node, InjuryPhase phase,
		Player<?> owner, Player<?> defender) {
		if (!(context.getTypeServer() instanceof InjuryTypeBlock)) {
			return true;
		}
		switch (((InjuryTypeBlock) context.getTypeServer()).getMode()) {
			case DO_NOT_USE_MODIFIERS:
				return false;
			case USE_ARMOUR_MODIFIERS_ONLY_AGAINST_TEAM_MATES:
				if (phase != InjuryPhase.ARMOUR) {
					return false;
				}
				break;
			case REGULAR:
				if (owner.getTeam() == defender.getTeam()) {
					return false;
				}
				break;
			default:
				break;
		}
		return phase != InjuryPhase.ARMOUR
			|| UtilGameOption.isOptionEnabled(context.getGame(), GameOptionId.MB_STACKS_AGAINST_CHAINSAW)
			|| Arrays.stream(node.getContext().getArmorModifiers())
			.noneMatch(modifier -> modifier.isRegisteredToSkillWithProperty(NamedProperties.blocksLikeChainsaw));
	}
}
