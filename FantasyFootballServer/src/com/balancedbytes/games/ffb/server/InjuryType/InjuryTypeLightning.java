package com.balancedbytes.games.ffb.server.InjuryType;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.factory.InjuryModifierFactory;
import com.balancedbytes.games.ffb.factory.SkillFactory;
import com.balancedbytes.games.ffb.injury.Lightning;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.DiceRoller;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.IStep;

import java.util.Arrays;
import java.util.Optional;

public class InjuryTypeLightning extends InjuryTypeServer<Lightning> {
	public InjuryTypeLightning() {
		super(new Lightning());
	}

	@Override
	public InjuryContext handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
			Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext,
			ApothecaryMode pApothecaryMode) {

		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();
		SkillFactory factory = game.getFactory(FactoryType.Factory.SKILL);
		Optional<Skill> foundSkill = factory.getSkills().stream().filter(skill -> skill.hasSkillProperty(NamedProperties.affectsEitherArmourOrInjuryOnBlock)).findFirst();

		if (!injuryContext.isArmorBroken()) {
			injuryContext.setArmorRoll(diceRoller.rollArmour());
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			if (!injuryContext.isArmorBroken()) {
				foundSkill.ifPresent(skill -> skill.getArmorModifiers().forEach(injuryContext::addArmorModifier));
				injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			}
		}

		if (injuryContext.isArmorBroken()) {
			injuryContext.setInjuryRoll(diceRoller.rollInjury());
			injuryContext.addInjuryModifier(((InjuryModifierFactory)game.getFactory(FactoryType.Factory.INJURY_MODIFIER)).getNigglingInjuryModifier(pDefender));

			if (Arrays.stream(injuryContext.getArmorModifiers())
				.noneMatch(modifier -> modifier.isRegisteredToSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnBlock))) {
				foundSkill.ifPresent(skill -> skill.getInjuryModifiers().forEach(injuryContext::addInjuryModifier));
			}

			setInjury(pDefender, gameState, diceRoller);
		} else {
			injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
		}

		return injuryContext;
	}
}
