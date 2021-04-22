package com.fumbbl.ffb.server.InjuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.InjuryContext;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.PilingOnInjury;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Arrays;
import java.util.Optional;

public class InjuryTypePilingOnInjury extends InjuryTypeServer<PilingOnInjury> {
	public InjuryTypePilingOnInjury() {
		super(new PilingOnInjury());
	}

	@Override
	public InjuryContext handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
	                                  Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext,
	                                  ApothecaryMode pApothecaryMode) {

		if (pOldInjuryContext != null) {
			for (ArmorModifier armorModifier : pOldInjuryContext.getArmorModifiers()) {
				injuryContext.addArmorModifier(armorModifier);
			}
		}

		injuryContext.setArmorBroken(true);

		injuryContext.setInjuryRoll(diceRoller.rollInjury());
		injuryContext.addInjuryModifier(((InjuryModifierFactory) game.getFactory(FactoryType.Factory.INJURY_MODIFIER)).getNigglingInjuryModifier(pDefender));

		if (!UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_DOES_NOT_STACK)) {
			Optional<Skill> availableSkill = Arrays.stream(UtilCards.findAllSkills(pAttacker))
				.filter(skill -> skill.hasSkillProperty(NamedProperties.affectsEitherArmourOrInjuryOnBlock)).findFirst();

			availableSkill.ifPresent(skill -> {
				if (Arrays.stream(injuryContext.getArmorModifiers())
					.noneMatch(modifier -> modifier.isRegisteredToSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnBlock))) {
					skill.getInjuryModifiers().forEach(injuryContext::addInjuryModifier);
				}
			});
		}

		setInjury(pDefender, gameState, diceRoller);


		return injuryContext;
	}
}