package com.balancedbytes.games.ffb.server.InjuryType;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.modifiers.InjuryModifier;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.factory.InjuryModifierFactory;
import com.balancedbytes.games.ffb.injury.Block;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.ArmorModifier;
import com.balancedbytes.games.ffb.modifiers.ArmorModifierFactory;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.DiceRoller;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.IStep;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

			ArmorModifierFactory armorModifierFactory = game.getFactory(FactoryType.Factory.ARMOUR_MODIFIER);

			Skill chainsaw = Optional.ofNullable(pAttacker.getSkillWithProperty(NamedProperties.blocksLikeChainsaw))
				.orElseGet(() ->
					pDefender.getSkillWithProperty(NamedProperties.blocksLikeChainsaw)
				);

			injuryContext.setArmorRoll(diceRoller.rollArmour());
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			if (chainsaw != null) {
				chainsaw.getArmorModifiers().forEach(injuryContext::addArmorModifier);
				injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			} else if (!injuryContext.isArmorBroken() && pAttacker.getTeam() != pDefender.getTeam()) {
				Set<ArmorModifier> armorModifiers = armorModifierFactory.findArmorModifiers(game, pAttacker, pDefender, isStab(),
					isFoul());
				if (!armorModifiers.isEmpty()) {
					Set<ArmorModifier> reducedModifiers = armorModifiers.stream().filter(modifier -> !modifier.isRegisteredToSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnBlock)).collect(Collectors.toSet());
					if (!reducedModifiers.isEmpty() && reducedModifiers.size() < armorModifiers.size()) {
						injuryContext.addArmorModifiers(reducedModifiers);
						injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
					}

					if (!injuryContext.isArmorBroken()) {
						injuryContext.addArmorModifiers(armorModifiers);
						injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
					}
				}
			}
		}

		if (injuryContext.isArmorBroken()) {
			InjuryModifierFactory factory = game.getFactory(FactoryType.Factory.INJURY_MODIFIER);
			injuryContext.setInjuryRoll(diceRoller.rollInjury());
			injuryContext.addInjuryModifier(factory.getNigglingInjuryModifier(pDefender));

			// do not use injuryModifiers on blocking own team-mate with b&c
			if (pAttacker.getTeam() != pDefender.getTeam()) {
				Set<InjuryModifier> armorModifiers = factory.findInjuryModifiers(game, injuryContext, pAttacker,
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