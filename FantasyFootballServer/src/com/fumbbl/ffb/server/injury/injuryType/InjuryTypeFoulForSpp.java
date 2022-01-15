package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.ArmorModifierFactory;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.FoulForSpp;
import com.fumbbl.ffb.injury.FoulForSppWithChainsaw;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.modifiers.ArmorModifierContext;
import com.fumbbl.ffb.modifiers.InjuryModifier;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Optional;
import java.util.Set;

public class InjuryTypeFoulForSpp extends ModificationAwareInjuryTypeServer<FoulForSpp> {
	private final boolean useChainsaw;

	public InjuryTypeFoulForSpp(boolean useChainsaw) {
		super(useChainsaw ? new FoulForSppWithChainsaw() : new FoulForSpp());
		this.useChainsaw = useChainsaw;
	}

	@Override
	protected void injuryRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender, InjuryContext injuryContext) {
		InjuryModifierFactory factory = game.getFactory(FactoryType.Factory.INJURY_MODIFIER);
		injuryContext.setInjuryRoll(diceRoller.rollInjury());

		Set<InjuryModifier> armorModifiers = factory.findInjuryModifiers(game, injuryContext, pAttacker,
			pDefender, isStab(), isFoul(), isVomit());
		injuryContext.addInjuryModifiers(armorModifiers);

		setInjury(pDefender, gameState, diceRoller, injuryContext);
	}

	@Override
	protected void armourRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender,
	                          DiceInterpreter diceInterpreter, InjuryContext injuryContext, boolean roll) {
		// Blatant Foul breaks armor without roll
		if (game.isActive(NamedProperties.foulBreaksArmourWithoutRoll)) {
			injuryContext.setArmorBroken(true);
		}

		if (!injuryContext.isArmorBroken()) {

			if (roll) {
				injuryContext.setArmorRoll(diceRoller.rollArmour());
			}

			if (useChainsaw) {
				Optional<Skill> attackerHasChainsaw = Optional.ofNullable(pAttacker.getSkillWithProperty(NamedProperties.blocksLikeChainsaw));
				attackerHasChainsaw.ifPresent(skill -> skill.getArmorModifiers().forEach(injuryContext::addArmorModifier));
			}

			ArmorModifierFactory armorModifierFactory = game.getFactory(FactoryType.Factory.ARMOUR_MODIFIER);
			ArmorModifierContext context = new ArmorModifierContext(game, pAttacker, pDefender, false, true, UtilPlayer.findFoulAssists(game, pAttacker, pDefender));

			armorModifierFactory.getFoulAssist(context).forEach(injuryContext::addArmorModifier);

			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));

			if (!injuryContext.isArmorBroken()) {
				Set<ArmorModifier> armorModifiers = armorModifierFactory.findArmorModifiers(game, pAttacker, pDefender, isStab(),
					isFoul());
				injuryContext.addArmorModifiers(armorModifiers);
				injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			}

		}
	}
}
