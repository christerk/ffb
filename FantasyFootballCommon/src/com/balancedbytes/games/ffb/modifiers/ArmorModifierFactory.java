package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.factory.INamedObjectFactory;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.ARMOUR_MODIFIER)
@RulesCollection(RulesCollection.Rules.COMMON)
public class ArmorModifierFactory implements INamedObjectFactory<ArmorModifier> {

	private ModifierAggregator modifierAggregator;

	private final Set<ArmorModifier> armorModifiers = new HashSet<ArmorModifier>() {{
		add(new FoulAssistArmorModifier("1 Offensive Assist", 1, true));
		add(new FoulAssistArmorModifier("2 Offensive Assists", 2, true));
		add(new FoulAssistArmorModifier("3 Offensive Assists", 3, true));
		add(new FoulAssistArmorModifier("4 Offensive Assists", 4, true));
		add(new FoulAssistArmorModifier("5 Offensive Assists", 5, true));
		add(new FoulAssistArmorModifier("6 Offensive Assists", 6, true));
		add(new FoulAssistArmorModifier("7 Offensive Assists", 7, true));
		add(new FoulAssistArmorModifier("1 Defensive Assist", -1, true));
		add(new FoulAssistArmorModifier("2 Defensive Assists", -2, true));
		add(new FoulAssistArmorModifier("3 Defensive Assists", -3, true));
		add(new FoulAssistArmorModifier("4 Defensive Assists", -4, true));
		add(new FoulAssistArmorModifier("5 Defensive Assists", -5, true));
		add(new StaticArmourModifier("Foul", 1, false) {
			@Override
			public boolean appliesToContext(ArmorModifierContext context) {
				Game game = context.getGame();
				return
					context.isFoul()
						&& (UtilGameOption.isOptionEnabled(game, GameOptionId.FOUL_BONUS)
						|| (UtilGameOption.isOptionEnabled(game, GameOptionId.FOUL_BONUS_OUTSIDE_TACKLEZONE)
						&& (UtilPlayer.findTacklezones(game, context.getAttacker()) < 1)));
			}
		});
		add(new SpecialEffectArmourModifier("Bomb", 1 , false, SpecialEffect.BOMB));
		add(new SpecialEffectArmourModifier("Fireball", 1 , false, SpecialEffect.FIREBALL));
		add(new SpecialEffectArmourModifier("Lightning", 1 , false, SpecialEffect.LIGHTNING));
	}};

	public ArmorModifier forName(String name) {
		return Stream.concat(armorModifiers.stream(), modifierAggregator.getArmourModifiers().stream())
			.filter(modifier -> modifier.getName().equals(name))
			.findFirst()
			.orElse(null);
	}

	public Set<ArmorModifier> findArmorModifiers(Game game, Player<?> attacker, Player<?> defender, boolean isStab,
	                                             boolean isFoul) {

		ArmorModifierContext context = new ArmorModifierContext(game, attacker, defender, isStab, isFoul);
		Set<ArmorModifier> armorModifiers = getArmorModifiers(attacker, context);

		if (UtilGameOption.isOptionEnabled(game, GameOptionId.CLAW_DOES_NOT_STACK)
			&& armorModifiers.stream().anyMatch(modifier -> modifier.isRegisteredToSkillWithProperty(NamedProperties.reducesArmourToFixedValue))
		) {
			return armorModifiers.stream()
				.filter(modifier -> !(modifier.isRegisteredToSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnBlock)))
				.collect(Collectors.toSet());
		}

		return armorModifiers;
	}

	public Set<SpecialEffectArmourModifier> specialEffectArmourModifiers(SpecialEffect specialEffect) {
		return armorModifiers.stream().filter(modifier -> modifier instanceof SpecialEffectArmourModifier)
			.map(modifier -> (SpecialEffectArmourModifier) modifier)
			.filter(modifier -> modifier.getEffect() == specialEffect)
			.collect(Collectors.toSet());
	}

	public Set<ArmorModifier> getFoulAssist(ArmorModifierContext context) {
		return armorModifiers.stream().filter(modifier -> !(modifier instanceof SpecialEffectArmourModifier) && modifier.appliesToContext(context)).collect(Collectors.toSet());
	}

	public ArmorModifier[] toArray(Set<ArmorModifier> pArmorModifiers) {
		if (pArmorModifiers != null) {
			ArmorModifier[] modifierArray = pArmorModifiers.toArray(new ArmorModifier[0]);
			Arrays.sort(modifierArray, Comparator.comparing(ArmorModifier::getName));
			return modifierArray;
		} else {
			return new ArmorModifier[0];
		}
	}

	@Override
	public void initialize(Game game) {
		modifierAggregator = game.getModifierAggregator();
	}

	private Set<ArmorModifier> getArmorModifiers(Player<?> player, ArmorModifierContext context) {
		return Arrays.stream(UtilCards.findAllSkills(player))
			.flatMap(skill -> skill.getArmorModifiers().stream())
			.filter(modifier -> modifier.appliesToContext(context))
			.collect(Collectors.toSet());
	}

	private static class FoulAssistArmorModifier extends StaticArmourModifier {

		public FoulAssistArmorModifier(String pName, int pModifier, boolean pFoulAssistModifier) {
			super(pName, pModifier, pFoulAssistModifier);
		}

		@Override
		public boolean appliesToContext(ArmorModifierContext context) {
			return context.isFoul() && context.getFoulAssists() == getModifier(context.getAttacker());
		}
	}
}
