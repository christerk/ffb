package com.balancedbytes.games.ffb.server.InjuryType;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.factory.InjuryModifierFactory;
import com.balancedbytes.games.ffb.factory.SkillFactory;
import com.balancedbytes.games.ffb.injury.Chainsaw;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.DiceRoller;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.IStep;

public class InjuryTypeChainsaw extends InjuryTypeServer<Chainsaw> {
	public InjuryTypeChainsaw() {
		super(new Chainsaw());
	}

	@Override
	public InjuryContext handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
			Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext,
			ApothecaryMode pApothecaryMode) {

		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

		if (!injuryContext.isArmorBroken()) {
			injuryContext.setArmorRoll(diceRoller.rollArmour());
			SkillFactory factory = game.getFactory(FactoryType.Factory.SKILL);
			factory.getSkills().stream()
				.filter(skill -> skill.hasSkillProperty(NamedProperties.blocksLikeChainsaw))
				.flatMap(skill -> skill.getArmorModifiers().stream())
				.forEach(injuryContext::addArmorModifier);
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
		}

		if (injuryContext.isArmorBroken()) {
			injuryContext.setInjuryRoll(diceRoller.rollInjury());
			injuryContext.addInjuryModifier(((InjuryModifierFactory)game.getFactory(FactoryType.Factory.INJURY_MODIFIER)).getNigglingInjuryModifier(pDefender));
			setInjury(pDefender, gameState, diceRoller);
		} else {
			injuryContext.setInjury(null);
		}
		return injuryContext;
	}
}