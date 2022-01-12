package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.injury.BlockProne;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;

import java.util.HashSet;

public class InjuryTypeBlockProne extends ModificationAwareInjuryTypeServer<BlockProne> {
	public InjuryTypeBlockProne() {
		super(new BlockProne());
	}

	@Override
	protected void injuryRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender, InjuryContext injuryContext) {
		injuryContext.setInjuryRoll(diceRoller.rollInjury());
		Skill stunty = pDefender.getSkillWithProperty(NamedProperties.isHurtMoreEasily);
		if (stunty != null) {
			injuryContext.addInjuryModifiers(new HashSet<>(stunty.getInjuryModifiers()));
		}
		setInjury(pDefender, gameState, diceRoller);
	}

	@Override
	protected void armourRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender, DiceInterpreter diceInterpreter, InjuryContext injuryContext, boolean roll) {
		if (roll) {
			injuryContext.setArmorRoll(diceRoller.rollArmour());
		}
		injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
	}
}