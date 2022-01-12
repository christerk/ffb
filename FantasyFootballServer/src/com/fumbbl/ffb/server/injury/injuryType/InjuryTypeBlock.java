package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.ArmorModifierFactory;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.modifiers.InjuryModifier;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class InjuryTypeBlock extends ModificationAwareInjuryTypeServer<Block> {
	private final Mode mode;
	private final boolean allowAttackerChainsaw;

	public InjuryTypeBlock() {
		this(Mode.REGULAR, true);
	}

	public InjuryTypeBlock(Mode mode) {
		this(mode, true);
	}

	public InjuryTypeBlock(Mode mode, boolean allowAttackerChainsaw) {
		super(new Block());
		this.mode = mode;
		this.allowAttackerChainsaw = allowAttackerChainsaw;
	}

	@Override
	protected void injuryRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender, InjuryContext injuryContext) {
		InjuryModifierFactory factory = game.getFactory(FactoryType.Factory.INJURY_MODIFIER);
		injuryContext.setInjuryRoll(diceRoller.rollInjury());
		factory.getNigglingInjuryModifier(pDefender).ifPresent(injuryContext::addInjuryModifier);

		Skill stunty = pDefender.getSkillWithProperty(NamedProperties.isHurtMoreEasily);
		if (stunty != null) {
			injuryContext.addInjuryModifiers(new HashSet<>(stunty.getInjuryModifiers()));
		}
		// do not use injuryModifiers on blocking own team-mate with b&c
		if (mode == Mode.USE_MODIFIERS_AGAINST_TEAM_MATES || (mode != Mode.DO_NOT_USE_MODIFIERS && pAttacker.getTeam() != pDefender.getTeam())) {
			Set<InjuryModifier> injuryModifiers = factory.findInjuryModifiersWithoutNiggling(game, injuryContext, pAttacker,
				pDefender, isStab(), isFoul(), isVomit());
			injuryContext.addInjuryModifiers(injuryModifiers);
		}

		setInjury(pDefender, gameState, diceRoller, injuryContext);
	}

	@Override
	protected void armourRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender,
	                          DiceInterpreter diceInterpreter, InjuryContext injuryContext, boolean roll) {
		if (!injuryContext.isArmorBroken()) {

			ArmorModifierFactory armorModifierFactory = game.getFactory(FactoryType.Factory.ARMOUR_MODIFIER);

			Skill chainsaw = allowAttackerChainsaw ? pAttacker.getSkillWithProperty(NamedProperties.blocksLikeChainsaw) : null;
			if (chainsaw == null) {
				chainsaw = pDefender.getSkillWithProperty(NamedProperties.blocksLikeChainsaw);
			}

			if (roll) {
				injuryContext.setArmorRoll(diceRoller.rollArmour());
			}
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			if (chainsaw != null) {
				chainsaw.getArmorModifiers().forEach(injuryContext::addArmorModifier);
				injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			} else if (!injuryContext.isArmorBroken() && (mode == Mode.USE_MODIFIERS_AGAINST_TEAM_MATES || (mode != Mode.DO_NOT_USE_MODIFIERS && pAttacker.getTeam() != pDefender.getTeam()))) {
				Set<ArmorModifier> armorModifiers = armorModifierFactory.findArmorModifiers(game, pAttacker, pDefender, isStab(),
					isFoul());
				Optional<ArmorModifier> claw = armorModifiers.stream()
					.filter(modifier -> modifier.isRegisteredToSkillWithProperty(NamedProperties.reducesArmourToFixedValue)).findFirst();
				if (claw.isPresent()) {
					injuryContext.addArmorModifier(claw.get());
					injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
					if (!injuryContext.isArmorBroken()) {
						if (UtilGameOption.isOptionEnabled(game, GameOptionId.CLAW_DOES_NOT_STACK)) {
							armorModifiers.remove(claw.get());
							injuryContext.clearArmorModifiers();
							injuryContext.addArmorModifiers(armorModifiers);
							injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
							if (!injuryContext.isArmorBroken()) {
								// set claw as modifier as it should be displayed as used in the log when there is no stacking to avoid confusion
								injuryContext.clearArmorModifiers();
								injuryContext.addArmorModifier(claw.get());
							}
						} else {
							injuryContext.addArmorModifiers(armorModifiers);
						}
						injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
					}
				} else {
					injuryContext.addArmorModifiers(armorModifiers);
					injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));

				}
			}
		}
	}

	public enum Mode {
		REGULAR, USE_MODIFIERS_AGAINST_TEAM_MATES, DO_NOT_USE_MODIFIERS
	}
}