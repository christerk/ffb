package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.injury.TTMHitPlayerForSpp;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifierContext;
import com.fumbbl.ffb.modifiers.InjuryModifierContext;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Optional;

public class InjuryTypeTTMHitPlayerForSpp extends ModificationAwareInjuryTypeServer<TTMHitPlayerForSpp> {
	public InjuryTypeTTMHitPlayerForSpp() {
		super(new TTMHitPlayerForSpp());
	}

	@Override
	protected void armourRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker,
		Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate,
		DiceInterpreter diceInterpreter, InjuryContext injuryContext, boolean roll) {

		Optional<Skill> lethalFlight = UtilCards.getSkillWithProperty(pAttacker, NamedProperties.affectsEitherArmourOrInjuryOnTtm);

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

			if (!injuryContext.isArmorBroken() && lethalFlight.isPresent()
				&& !UtilCards.hasUnusedSkillWithProperty(pDefender, NamedProperties.ignoresArmourModifiersFromSkills)) {
				lethalFlight.get().getArmorModifiers().stream()
					.filter(mod -> mod.appliesToContext(new ArmorModifierContext(game, pAttacker, pDefender, false, false, 0, true)))
					.forEach(injuryContext::addArmorModifier);
				injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			}
		}
	}

	@Override
	protected void injuryRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker,
		Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate,
		InjuryContext injuryContext) {

		Optional<Skill> lethalFlight = UtilCards.getSkillWithProperty(pAttacker, NamedProperties.affectsEitherArmourOrInjuryOnTtm);

		if (lethalFlight.isPresent()
			&& lethalFlight.get().getArmorModifiers().stream().anyMatch(injuryContext::hasArmorModifier)) {
			lethalFlight = Optional.empty();
		}

		injuryContext.setInjuryRoll(diceRoller.rollInjury());

		lethalFlight.ifPresent(skill -> skill.getInjuryModifiers().stream()
			.filter(mod -> mod.appliesToContext(new InjuryModifierContext(game, injuryContext, pAttacker, pDefender, false, false, false, false, true)))
			.forEach(injuryContext::addInjuryModifier));

		setInjury(pDefender, gameState, diceRoller, injuryContext);
	}
}
