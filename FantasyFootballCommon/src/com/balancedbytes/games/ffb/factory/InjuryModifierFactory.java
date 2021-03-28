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
import com.balancedbytes.games.ffb.modifiers.StaticInjuryModifier;
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

	private final Set<StaticInjuryModifier> niggleModifiers = new HashSet<StaticInjuryModifier>() {{
		add(new StaticInjuryModifier("1 Niggling Injury", 1, true));
		add(new StaticInjuryModifier("2 Niggling Injuries", 2, true));
		add(new StaticInjuryModifier("3 Niggling Injuries", 3, true));
		add(new StaticInjuryModifier("4 Niggling Injuries", 4, true));
		add(new StaticInjuryModifier("5 Niggling Injuries", 5, true));
	}};

	public InjuryModifier forName(String name) {
		return Stream.concat(niggleModifiers.stream(), modifierAggregator.getInjuryModifiers().stream())
			.filter(modifier -> modifier.getName().equals(name))
			.findFirst()
			.orElse(null);	}

	public ModifiersWithContext findInjuryModifiers(Game game, InjuryContext injuryContext, Player<?> attacker,
			Player<?> defender, boolean isStab, boolean isFoul) {

		InjuryModifierContext context = new InjuryModifierContext(game, injuryContext, attacker, defender, isStab, isFoul);

		return new ModifiersWithContext(getInjuryModifiers(context), context);
	}

	public InjuryModifier getNigglingInjuryModifier(Player<?> pPlayer) {
		if (pPlayer != null) {
			long nigglingInjuries = Arrays.stream(pPlayer.getLastingInjuries()).filter(seriousInjury -> seriousInjury.getInjuryAttribute() == InjuryAttribute.NI).count();

			for (StaticInjuryModifier modifier : niggleModifiers) {
				if (modifier.isNigglingInjuryModifier() && (modifier.getModifier(null) == nigglingInjuries)) {
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

	public static class ModifiersWithContext {
		private final Set<InjuryModifier> modifiers;
		private final InjuryModifierContext context;

		public ModifiersWithContext(Set<InjuryModifier> modifiers, InjuryModifierContext context) {
			this.modifiers = modifiers;
			this.context = context;
		}

		public Set<InjuryModifier> getModifiers() {
			return modifiers;
		}

		public InjuryModifierContext getContext() {
			return context;
		}
	}
}
