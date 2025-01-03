package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.IInjuryContextModification;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.injury.context.InjuryModification;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.mechanic.RollMechanic;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Objects;
import java.util.Set;

public abstract class InjuryContextModification<T extends ModificationParams> implements IInjuryContextModification {

	private Skill skill;
	private final Set<Class<? extends InjuryType>> validInjuryTypes;

	public InjuryContextModification(Set<Class<? extends InjuryType>> validInjuryTypes) {
		this.validInjuryTypes = validInjuryTypes;
	}

	protected abstract T params(GameState gameState, ModifiedInjuryContext newContext, InjuryType injuryType);

	public boolean modifyArmour(GameState gameState, InjuryContext injuryContext, InjuryType injuryType) {
		ModifiedInjuryContext newContext = context(injuryContext);
		T params = params(gameState, newContext, injuryType);

		Player<?> defender = gameState.getGame().getPlayerById(injuryContext.fDefenderId);

		if (!UtilCards.hasUnusedSkillWithProperty(defender, NamedProperties.ignoresArmourModifiersFromSkills)
			&& allowedForAttackerAndDefenderTeams(gameState.getGame(), newContext)
			&& tryArmourRollModification(params)
			&& modifyArmourInternal(params)) {
			newContext.setModification(InjuryModification.ARMOUR);
			newContext.setUsedSkill(skill);
			injuryContext.setModifiedInjuryContext(newContext);
			return true;
		}
		return false;
	}

	protected boolean tryArmourRollModification(T params) {
		return false;
	}

	protected boolean modifyArmourInternal(T params) {

		prepareArmourParams(params);
		params.getNewContext().setArmorBroken(params.getDiceInterpreter().isArmourBroken(params.getGameState(), params.getNewContext()));

		if (armourModificationCantHelp(params)) {
			return false;
		}

		ModifiedInjuryContext newContext = params.getNewContext();
		newContext.clearArmorModifiers();
		applyArmourModification(params);
		newContext.setArmorBroken(params.getDiceInterpreter().isArmourBroken(params.getGameState(), newContext));

		return true;
	}

	protected void prepareArmourParams(T params) {
		applyArmourModification(params);
	}

	protected boolean armourModificationCantHelp(T params) {
		return !params.getNewContext().isArmorBroken();
	}

	protected void applyArmourModification(T params) {
		params.getNewContext().addArmorModifiers(skill.getArmorModifiers());
	}

	public boolean modifyInjury(GameState gameState, InjuryContext injuryContext, InjuryType injuryType) {
		if (tryInjuryModification(gameState.getGame(), injuryContext, injuryType)) {
			ModifiedInjuryContext newContext = context(injuryContext);
			if (allowedForAttackerAndDefenderTeams(gameState.getGame(), injuryContext) &&
				modifyInjuryInternal(newContext, gameState)) {
				newContext.setModification(InjuryModification.INJURY);
				newContext.setUsedSkill(skill);
				injuryContext.setModifiedInjuryContext(newContext);
				return true;
			}
		}
		return false;
	}

	protected boolean tryInjuryModification(Game game, InjuryContext injuryContext, InjuryType injuryType) {
		return false;
	}

	protected boolean modifyInjuryInternal(ModifiedInjuryContext injuryContext, GameState gameState) {
		injuryContext.addInjuryModifiers(getSkill().getInjuryModifiers());
		PlayerState newInjury = interpretInjury(gameState, injuryContext);

		if (!Objects.equals(newInjury, injuryContext.fInjury)) {
			injuryContext.setInjury(newInjury);
			return true;
		}

		return false;
	}

	@Override
	public boolean requiresConditionalReRollSkill() {
		return false;
	}

	abstract SkillUse skillUse();

	public boolean isValidType(InjuryType injuryType) {
		return validInjuryTypes.contains(injuryType.getClass());
	}

	public Skill getSkill() {
		return skill;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	protected PlayerState interpretInjury(GameState gameState, InjuryContext injuryContext) {

		RollMechanic rollMechanic = (RollMechanic) gameState.getGame().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ROLL.name());

		return rollMechanic.interpretInjuryRoll(gameState.getGame(), injuryContext);
	}

	protected boolean allowedForAttackerAndDefenderTeams(Game game, InjuryContext injuryContext) {
		return differentTeams(game, injuryContext);
	}

	protected boolean differentTeams(Game game, InjuryContext injuryContext) {
		Player<?> attacker = game.getPlayerById(injuryContext.fAttackerId);
		Player<?> defender = game.getPlayerById(injuryContext.fDefenderId);

		return attacker == null || attacker.getTeam() != defender.getTeam();
	}

	private ModifiedInjuryContext context(InjuryContext context) {
		if (context.getModifiedInjuryContext() != null) {
			return context.getModifiedInjuryContext();
		}
		return newContext(context);
	}

	private ModifiedInjuryContext newContext(InjuryContext injuryContext) {
		ModifiedInjuryContext newContext = new ModifiedInjuryContext();
		newContext.fInjuryType = injuryContext.fInjuryType;
		newContext.fArmorModifiers.addAll(injuryContext.fArmorModifiers);
		newContext.fInjuryModifiers.addAll(injuryContext.fInjuryModifiers);
		newContext.casualtyModifiers.addAll(injuryContext.casualtyModifiers);
		newContext.fAttackerId = injuryContext.fAttackerId;
		newContext.fDefenderId = injuryContext.fDefenderId;
		newContext.fDefenderPosition = injuryContext.fDefenderPosition;
		newContext.fArmorBroken = injuryContext.fArmorBroken;
		if (injuryContext.fArmorRoll != null) {
			newContext.fArmorRoll = new int[2];
			System.arraycopy(injuryContext.fArmorRoll, 0, newContext.fArmorRoll, 0, 2);
		}
		if (injuryContext.fInjuryRoll != null) {
			newContext.fInjuryRoll = new int[2];
			System.arraycopy(injuryContext.fInjuryRoll, 0, newContext.fInjuryRoll, 0, 2);
		}
		newContext.fInjury = injuryContext.fInjury;
		newContext.fApothecaryMode = injuryContext.fApothecaryMode;
		newContext.fApothecaryStatus = injuryContext.fApothecaryStatus;
		newContext.setSkillUse(skillUse());
		return newContext;
	}
}
