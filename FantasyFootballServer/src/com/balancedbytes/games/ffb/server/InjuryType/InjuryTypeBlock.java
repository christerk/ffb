package com.balancedbytes.games.ffb.server.InjuryType;

import java.util.Set;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.ArmorModifier;
import com.balancedbytes.games.ffb.ArmorModifierFactory;
import com.balancedbytes.games.ffb.ArmorModifiers;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.InjuryModifier;
import com.balancedbytes.games.ffb.InjuryModifierFactory;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.injury.Block;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.DiceRoller;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.util.UtilCards;

public class InjuryTypeBlock extends InjuryTypeServer<Block> {
	public InjuryTypeBlock() {
		super(new Block());
	}

	@Override
	public InjuryContext handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
			Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext,
			ApothecaryMode pApothecaryMode) {

		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

		if (!injuryContext.isArmorBroken()) {

			ArmorModifierFactory modifierFactory = new ArmorModifierFactory();

			boolean attackerHasChainsaw = UtilCards.hasSkillWithProperty(pAttacker, NamedProperties.blocksLikeChainsaw);
			boolean defenderHasChainsaw = UtilCards.hasSkillWithProperty(pDefender, NamedProperties.blocksLikeChainsaw);
			boolean chainsawIsInvolved = (attackerHasChainsaw || defenderHasChainsaw);

			injuryContext.setArmorRoll(diceRoller.rollArmour());
			if (chainsawIsInvolved) {
				injuryContext.addArmorModifier(ArmorModifiers.CHAINSAW);
			}
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			// do not use armorModifiers on blocking own team-mate
			if (pAttacker.getTeam() != pDefender.getTeam()) {

				injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));

				if (!injuryContext.isArmorBroken()) {
					Set<ArmorModifier> armorModifiers = modifierFactory.findArmorModifiers(game, pAttacker, pDefender, isStab(),
							isFoul());
					injuryContext.addArmorModifiers(armorModifiers);
					injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
				}
			}
		}

		if (injuryContext.isArmorBroken()) {
			injuryContext.setInjuryRoll(diceRoller.rollInjury());
			injuryContext.addInjuryModifier(new InjuryModifierFactory().getNigglingInjuryModifier(pDefender));

			// do not use injuryModifiers on blocking own team-mate with b&c
			if (pAttacker.getTeam() != pDefender.getTeam()) {
				InjuryModifierFactory modifierFactory = new InjuryModifierFactory();
				Set<InjuryModifier> armorModifiers = modifierFactory.findInjuryModifiers(game, injuryContext, pAttacker,
						pDefender, isStab(), isFoul());
				injuryContext.addInjuryModifiers(armorModifiers);
			}

			setInjury(pDefender, gameState, diceRoller);
		} else {
			injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
		}
		return injuryContext;
	}
}