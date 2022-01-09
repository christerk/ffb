package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.injury.BlockStunned;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;

import java.util.HashSet;

public class InjuryTypeBlockStunned extends InjuryTypeServer<BlockStunned> {
	public InjuryTypeBlockStunned() {
		super(new BlockStunned());
	}

	@Override
	public InjuryContext handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
	                                  Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate, InjuryContext pOldInjuryContext,
	                                  ApothecaryMode pApothecaryMode) {

		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

		injuryContext.setArmorRoll(diceRoller.rollArmour());
		injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));

		if (injuryContext.isArmorBroken()) {
			injuryContext.setInjuryRoll(diceRoller.rollInjury());
			Skill stunty = pDefender.getSkillWithProperty(NamedProperties.isHurtMoreEasily);
			if (stunty != null) {
				injuryContext.addInjuryModifiers(new HashSet<>(stunty.getInjuryModifiers()));
			}
			setInjury(pDefender, gameState, diceRoller);
		} else {
			injuryContext.setInjury(new PlayerState(PlayerState.STUNNED));
		}

		return injuryContext;
	}
}