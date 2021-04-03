package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.InjuryAttribute;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.modifiers.InjuryModifier;
import com.balancedbytes.games.ffb.modifiers.InjuryModifierContext;
import com.balancedbytes.games.ffb.modifiers.ModifierAggregator;
import com.balancedbytes.games.ffb.modifiers.SpecialEffectInjuryModifier;
import com.balancedbytes.games.ffb.util.Scanner;
import com.balancedbytes.games.ffb.util.UtilCards;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.INJURY_MODIFIER)
@RulesCollection(Rules.COMMON)
public class InjuryModifierFactory implements INamedObjectFactory<InjuryModifier> {

	private ModifierAggregator modifierAggregator;

	private InjuryModifiers injuryModifiers;

	public InjuryModifier forName(String name) {
		return Stream.concat(injuryModifiers.values(), modifierAggregator.getInjuryModifiers().stream())
			.filter(modifier -> modifier.getName().equals(name))
			.findFirst()
			.orElse(null);	}

	public Set<InjuryModifier> findInjuryModifiers(Game game, InjuryContext injuryContext, Player<?> attacker,
			Player<?> defender, boolean isStab, boolean isFoul) {

		InjuryModifierContext context = new InjuryModifierContext(game, injuryContext, attacker, defender, isStab, isFoul);

		return getInjuryModifiers(context);
	}

	public InjuryModifier getNigglingInjuryModifier(Player<?> pPlayer) {
		if (pPlayer != null) {
			long nigglingInjuries = Arrays.stream(pPlayer.getLastingInjuries()).filter(seriousInjury -> seriousInjury.getInjuryAttribute() == InjuryAttribute.NI).count();

			return injuryModifiers.values().filter(modifier -> modifier.isNigglingInjuryModifier()
				&& (modifier.getModifier(null, null) == nigglingInjuries)).findFirst().orElse(null);
		}
		return null;
	}

	public Set<SpecialEffectInjuryModifier> specialEffectInjuryModifiers(SpecialEffect specialEffect) {
		return injuryModifiers.values().filter(modifier -> modifier instanceof SpecialEffectInjuryModifier)
			.map(modifier -> (SpecialEffectInjuryModifier) modifier)
			.filter(modifier -> modifier.getEffect() == specialEffect)
			.collect(Collectors.toSet());
	}

	public Set<InjuryModifier> getInjuryModifiers(InjuryModifierContext context) {
		return Stream.concat(
			Arrays.stream(UtilCards.findAllSkills(context.getAttacker())),
			Arrays.stream(UtilCards.findAllSkills(context.getDefender()))
		).flatMap(skill -> skill.getInjuryModifiers().stream())
			.filter(modifier -> modifier.appliesToContext(context))
			.collect(Collectors.toSet());
	}

	@Override
	public void initialize(Game game) {
		this.modifierAggregator = game.getModifierAggregator();
		injuryModifiers = new Scanner<>(InjuryModifiers.class).getInstancesImplementing(game.getOptions()).stream().findFirst().orElse(null);
	}

}
