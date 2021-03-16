package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.InjuryAttribute;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.modifiers.InjuryModifier;
import com.balancedbytes.games.ffb.modifiers.InjuryModifierContext;
import com.balancedbytes.games.ffb.modifiers.ModifierAggregator;
import com.balancedbytes.games.ffb.util.UtilCards;

import java.util.Arrays;
import java.util.HashSet;
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

	private final Set<InjuryModifier> niggleModifiers = new HashSet<InjuryModifier>() {{
		add(new InjuryModifier("1 Niggling Injury", 1, true));
		add(new InjuryModifier("2 Niggling Injuries", 2, true));
		add(new InjuryModifier("3 Niggling Injuries", 3, true));
		add(new InjuryModifier("4 Niggling Injuries", 4, true));
		add(new InjuryModifier("5 Niggling Injuries", 5, true));
	}};

	public InjuryModifier forName(String name) {
		return Stream.concat(niggleModifiers.stream(), modifierAggregator.getInjuryModifiers().stream())
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

			for (InjuryModifier modifier : niggleModifiers) {
				if (modifier.isNigglingInjuryModifier() && (modifier.getModifier() == nigglingInjuries)) {
					return modifier;
				}
			}
		}
		return null;
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
	}

}
