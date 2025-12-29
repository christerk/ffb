package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
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
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Optional;

public class InjuryTypeTTMHitPlayerForSpp extends InjuryTypeServer<TTMHitPlayerForSpp> {
	public InjuryTypeTTMHitPlayerForSpp() {
		super(new TTMHitPlayerForSpp());
	}

	@Override
	public void handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
	                         Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate, InjuryContext pOldInjuryContext,
	                         ApothecaryMode pApothecaryMode) {
		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

		Optional<Skill> lethalFlight = UtilCards.getSkillWithProperty(pAttacker, NamedProperties.affectsEitherArmourOrInjuryOnTtm);

		if (!injuryContext.isArmorBroken()) {
			injuryContext.setArmorRoll(diceRoller.rollArmour());
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
					.filter(mod -> mod.appliesToContext(new ArmorModifierContext(game, pAttacker, pDefender, false, false)))
					.forEach(injuryContext::addArmorModifier);
				injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
				lethalFlight = Optional.empty(); // consumed on armour
			}
		}

		if (injuryContext.isArmorBroken()) {
			injuryContext.setInjuryRoll(diceRoller.rollInjury());
			InjuryModifierFactory factory = game.getFactory(FactoryType.Factory.INJURY_MODIFIER);
			factory.findInjuryModifiers(game, injuryContext, pAttacker,
				pDefender, isStab(), isFoul(), isVomitLike()).forEach(injuryModifier -> injuryContext.addInjuryModifier(injuryModifier));

			lethalFlight.ifPresent(skill -> skill.getInjuryModifiers().stream()
				.filter(mod -> mod.appliesToContext(new InjuryModifierContext(game, injuryContext, pAttacker, pDefender, false, false, false)))
				.forEach(injuryContext::addInjuryModifier));

			setInjury(pDefender, gameState, diceRoller);

		} else {
			injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
		}
	}
}