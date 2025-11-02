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
import com.fumbbl.ffb.util.UtilCards;

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
		// do not use injuryModifiers on blocking own team-mate with b&c or for BB2025 USE_ARMOUR_MODIFIERS_ONLY mode
		if (mode != Mode.USE_ARMOUR_MODIFIERS_ONLY && (mode == Mode.USE_MODIFIERS_AGAINST_TEAM_MATES || (mode != Mode.DO_NOT_USE_MODIFIERS && pAttacker.getTeam() != pDefender.getTeam()))) {
			Set<InjuryModifier> injuryModifiers = factory.findInjuryModifiersWithoutNiggling(game, injuryContext, pAttacker,
				pDefender, isStab(), isFoul(), isVomitLike());
			injuryContext.addInjuryModifiers(injuryModifiers);
		}

		setInjury(pDefender, gameState, diceRoller, injuryContext);
	}

	@Override
	protected void armourRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender,
														DiceInterpreter diceInterpreter, InjuryContext injuryContext, boolean roll) {
		if (!injuryContext.isArmorBroken()) {

			ArmorModifierFactory armorModifierFactory = game.getFactory(FactoryType.Factory.ARMOUR_MODIFIER);

			Skill chainsaw = null;
			if (!UtilCards.hasUnusedSkillWithProperty(pDefender, NamedProperties.ignoresArmourModifiersFromSkills)) {
				chainsaw = allowAttackerChainsaw ? pAttacker.getSkillWithProperty(NamedProperties.blocksLikeChainsaw) : null;
				if (chainsaw == null) {
					chainsaw = pDefender.getSkillWithProperty(NamedProperties.blocksLikeChainsaw);
				}
			}

			if (roll) {
				injuryContext.setArmorRoll(diceRoller.rollArmour());
			}
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			if (chainsaw != null) {
				chainsaw.getArmorModifiers().forEach(injuryContext::addArmorModifier);
				injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			} else if (!injuryContext.isArmorBroken() && (mode == Mode.USE_ARMOUR_MODIFIERS_ONLY || mode == Mode.USE_MODIFIERS_AGAINST_TEAM_MATES || (mode != Mode.DO_NOT_USE_MODIFIERS && pAttacker.getTeam() != pDefender.getTeam()))) {
				Set<ArmorModifier> armorModifiers = armorModifierFactory.findArmorModifiers(game, pAttacker, pDefender, isStab(),
					isFoul());
				if (mode == Mode.USE_ARMOUR_MODIFIERS_ONLY) {
					// BB2025: Only apply Claws and Mighty Blow from attacker
					armorModifiers = armorModifiers.stream()
						.filter(modifier -> 
							modifier.isRegisteredToSkillWithProperty(NamedProperties.reducesArmourToFixedValue) || 
							modifier.isRegisteredToSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnBlock))
						.collect(java.util.stream.Collectors.toSet());
				}
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
		REGULAR, USE_MODIFIERS_AGAINST_TEAM_MATES, DO_NOT_USE_MODIFIERS, USE_ARMOUR_MODIFIERS_ONLY
	}
}