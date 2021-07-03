package com.fumbbl.ffb.server.InjuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.InjuryContext;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.TTMLanding;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;

import java.util.Optional;

public class InjuryTypeTTMLanding extends InjuryTypeServer<TTMLanding> {
	public InjuryTypeTTMLanding() {
		super(new TTMLanding());
	}

	@Override
	public InjuryContext handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
	                                  Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate, InjuryContext pOldInjuryContext,
	                                  ApothecaryMode pApothecaryMode) {

		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

		if (!injuryContext.isArmorBroken()) {
			injuryContext.setArmorRoll(diceRoller.rollArmour());
			Optional.ofNullable(pDefender.getSkillWithProperty(NamedProperties.blocksLikeChainsaw))
				.ifPresent(skill -> skill.getArmorModifiers().forEach(injuryContext::addArmorModifier));
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
		}

		if (injuryContext.isArmorBroken()) {
			injuryContext.setInjuryRoll(diceRoller.rollInjury());
			InjuryModifierFactory factory = game.getFactory(FactoryType.Factory.INJURY_MODIFIER);
			factory.findInjuryModifiers(game, injuryContext, pAttacker,
				pDefender, isStab(), isFoul()).forEach(injuryModifier -> injuryContext.addInjuryModifier(injuryModifier));

			setInjury(pDefender, gameState, diceRoller);

		} else {
			injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
		}
		return injuryContext;
	}
}