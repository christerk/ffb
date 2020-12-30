package com.balancedbytes.games.ffb.server.InjuryType;

import java.util.Set;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.ArmorModifier;
import com.balancedbytes.games.ffb.ArmorModifierFactory;
import com.balancedbytes.games.ffb.ArmorModifiers;
import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.InjuryModifier;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.factory.InjuryModifierFactory;
import com.balancedbytes.games.ffb.injury.Foul;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.DiceRoller;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

public class InjuryTypeFoul extends InjuryTypeServer<Foul> {
	public InjuryTypeFoul() {
		super(new Foul());
	}

	public boolean isCausedByOpponent() {
		return true;
	}

	@Override
	public InjuryContext handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
			Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext,
			ApothecaryMode pApothecaryMode) {

		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

		// Blatant Foul breaks armor without roll
		if (UtilCards.isCardActive(game, Card.BLATANT_FOUL)) {
			injuryContext.setArmorBroken(true);
		}

		if (!injuryContext.isArmorBroken()) {

			boolean attackerHasChainsaw = pAttacker.hasSkillWithProperty(NamedProperties.blocksLikeChainsaw);

			injuryContext.setArmorRoll(diceRoller.rollArmour());
			if (attackerHasChainsaw) {
				injuryContext.addArmorModifier(ArmorModifiers.CHAINSAW);
			}
			if (UtilGameOption.isOptionEnabled(game, GameOptionId.FOUL_BONUS)
					|| (UtilGameOption.isOptionEnabled(game, GameOptionId.FOUL_BONUS_OUTSIDE_TACKLEZONE)
							&& (UtilPlayer.findTacklezones(game, pAttacker) < 1))) {
				injuryContext.addArmorModifier(ArmorModifiers.FOUL);
			}
			int foulAssists = UtilPlayer.findFoulAssists(game, pAttacker, pDefender);
			if (foulAssists != 0) {
				ArmorModifier assistModifier = new ArmorModifierFactory().getFoulAssist(foulAssists);
				injuryContext.addArmorModifier(assistModifier);
			}
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));

			if (!injuryContext.isArmorBroken()) {
				ArmorModifierFactory modifierFactory = new ArmorModifierFactory();
				Set<ArmorModifier> armorModifiers = modifierFactory.findArmorModifiers(game, pAttacker, pDefender, isStab(),
						isFoul());
				injuryContext.addArmorModifiers(armorModifiers);
				injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			}

		}

		if (injuryContext.isArmorBroken()) {
			injuryContext.setInjuryRoll(diceRoller.rollInjury());
			injuryContext.addInjuryModifier(new InjuryModifierFactory().getNigglingInjuryModifier(pDefender));

			InjuryModifierFactory modifierFactory = new InjuryModifierFactory();
			Set<InjuryModifier> armorModifiers = modifierFactory.findInjuryModifiers(game, injuryContext, pAttacker,
					pDefender, isStab(), isFoul());
			injuryContext.addInjuryModifiers(armorModifiers);

			setInjury(pDefender, gameState, diceRoller);

		} else {
			injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
		}

		return injuryContext;
	}
}
