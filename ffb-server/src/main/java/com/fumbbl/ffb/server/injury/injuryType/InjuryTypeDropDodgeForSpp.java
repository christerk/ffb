package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.DropDodgeForSpp;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.injury.modification.bb2025.chain.OptionalSkillModifierService;
import com.fumbbl.ffb.util.UtilCards;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class InjuryTypeDropDodgeForSpp extends ModificationAwareInjuryTypeServer<DropDodgeForSpp> {

	private final Player<?> armBarPlayer;

	public InjuryTypeDropDodgeForSpp(Player<?> armBarPlayer) {
		super(new DropDodgeForSpp());
		this.armBarPlayer = armBarPlayer;
	}

	@Override
	protected void armourRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker,
		Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate,
		DiceInterpreter diceInterpreter, InjuryContext injuryContext, boolean roll) {

		OptionalSkillModifierService optionalService = new OptionalSkillModifierService();
		if (!optionalService.isOptional(game, armBar())) {
			injuryContext.setAttackerId(armBarPlayer.getId());
		}

		if (!injuryContext.isArmorBroken()) {
			if (roll) {
				injuryContext.setArmorRoll(diceRoller.rollArmour());
			}
			if (UtilCards.hasUnusedSkillWithProperty(pDefender, NamedProperties.ignoresArmourModifiersFromSkills)) {
				injuryContext.addArmorModifiers(pDefender.getSkillWithProperty(NamedProperties.ignoresArmourModifiersFromSkills).getArmorModifiers());
			} else {
				Optional.ofNullable(pDefender.getSkillWithProperty(NamedProperties.blocksLikeChainsaw))
					.ifPresent(skill -> skill.getArmorModifiers().forEach(injuryContext::addArmorModifier));
			}
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
		}

		Skill avOrInjModifierSkill = optionalService.unlessOptional(game, avOrInjModifierSkill(pDefender)).orElse(null);

		if (!injuryContext.isArmorBroken() && avOrInjModifierSkill != null) {
			avOrInjModifierSkill.getArmorModifiers().forEach(injuryContext::addArmorModifier);
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
		}
	}

	@Override
	protected void injuryRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker,
		Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate,
		InjuryContext injuryContext) {

		OptionalSkillModifierService optionalService = new OptionalSkillModifierService();
		if (!optionalService.isOptional(game, armBar())) {
			injuryContext.setAttackerId(armBarPlayer.getId());
		}

		Skill avOrInjModifierSkill = optionalService.unlessOptional(game, avOrInjModifierSkill(pDefender)).orElse(null);

		if (avOrInjModifierSkill != null
			&& avOrInjModifierSkill.getArmorModifiers().stream().anyMatch(injuryContext::hasArmorModifier)) {
			avOrInjModifierSkill = null;
		}

		injuryContext.setInjuryRoll(diceRoller.rollInjury());
		InjuryModifierFactory factory = game.getFactory(FactoryType.Factory.INJURY_MODIFIER);
		factory.findInjuryModifiers(game, injuryContext, pAttacker,
			pDefender, isStab(), isFoul(), isVomitLike()).forEach(injuryModifier -> injuryContext.addInjuryModifier(injuryModifier));
		if (avOrInjModifierSkill != null) {
			avOrInjModifierSkill.getInjuryModifiers().forEach(injuryContext::addInjuryModifier);
		}

		setInjury(pDefender, gameState, diceRoller, injuryContext);
	}

	@Override
	public List<Player<?>> optionalSkillAttackers(Game game, Player<?> defender, FieldCoordinate fromCoordinate) {
		if (avOrInjModifierSkill(defender) != null && new OptionalSkillModifierService().isOptional(game, armBar())) {
			return Collections.singletonList(armBarPlayer);
		}
		return Collections.emptyList();
	}

	@Override
	public boolean optionalSkillOwnerBecomesAttacker() {
		return true;
	}

	private Skill armBar() {
		return armBarPlayer.getSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnDodge);
	}

	private Skill avOrInjModifierSkill(Player<?> pDefender) {
		if (!UtilCards.hasUnusedSkillWithProperty(pDefender, NamedProperties.ignoresArmourModifiersFromSkills)) {
			return armBarPlayer.getSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnDodge);
		}

		return null;
	}
}
