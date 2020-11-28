package com.balancedbytes.games.ffb.server.InjuryType;

import com.balancedbytes.game.ffb.injury.PilingOnArmour;
import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.ArmorModifier;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.InjuryModifier;
import com.balancedbytes.games.ffb.InjuryModifierFactory;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.DiceRoller;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.model.ServerSkill;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.util.UtilCards;

public class InjuryTypePilingOnArmour extends InjuryTypeServer<PilingOnArmour>  {
	public InjuryTypePilingOnArmour() {
		super(new PilingOnArmour());
	}

	
	@Override
	public InjuryContext handleInjury(IStep step, Game game,GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender,
			FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext, ApothecaryMode pApothecaryMode) {
		
		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

		if (!injuryContext.isArmorBroken()) {
			boolean attackerHasChainsaw = UtilCards.hasSkillWithProperty(pAttacker,
					NamedProperties.blocksLikeChainsaw);
			boolean defenderHasChainsaw = UtilCards.hasSkillWithProperty(pDefender,
					NamedProperties.blocksLikeChainsaw);
			boolean chainsawIsInvolved = (attackerHasChainsaw || defenderHasChainsaw);

			injuryContext.setArmorRoll(diceRoller.rollArmour());
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			if (!UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_DOES_NOT_STACK)) {
				if (chainsawIsInvolved) {
					injuryContext.addArmorModifier(ArmorModifier.CHAINSAW);
				}
				if (UtilCards.hasSkill(game, pAttacker, ServerSkill.CLAW) && (pDefender.getArmour() > 7)
						&& !attackerHasChainsaw) {
					injuryContext.addArmorModifier(ArmorModifier.CLAWS);
				}
				injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
				if (!injuryContext.isArmorBroken() && UtilCards.hasSkill(game, pAttacker, ServerSkill.MIGHTY_BLOW)
						&& !attackerHasChainsaw && !(UtilCards.hasSkill(game, pAttacker, ServerSkill.CLAW)
								&& UtilGameOption.isOptionEnabled(game, GameOptionId.CLAW_DOES_NOT_STACK))) {
					injuryContext.addArmorModifier(ArmorModifier.MIGHTY_BLOW);
					injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
				}
			}
		}

		if (injuryContext.isArmorBroken()) {
			injuryContext.setInjuryRoll(diceRoller.rollInjury());
			injuryContext.addInjuryModifier(new InjuryModifierFactory().getNigglingInjuryModifier(pDefender));

			if (!UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_DOES_NOT_STACK)) {
				if (UtilCards.hasSkill(game, pAttacker, ServerSkill.MIGHTY_BLOW)
						&& !injuryContext.hasArmorModifier(ArmorModifier.MIGHTY_BLOW)) {
					injuryContext.addInjuryModifier(InjuryModifier.MIGHTY_BLOW);
				}
			}
			setInjury(pDefender, gameState, diceRoller);
		} else {
			injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
		}

		return injuryContext;
	}
}