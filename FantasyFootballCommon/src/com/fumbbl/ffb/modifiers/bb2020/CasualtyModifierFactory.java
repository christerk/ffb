package com.fumbbl.ffb.modifiers.bb2020;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.InjuryAttribute;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.ModifierAggregator;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@FactoryType(FactoryType.Factory.CASUALTY_MODIFIER)
@RulesCollection(RulesCollection.Rules.BB2020)
public class CasualtyModifierFactory implements INamedObjectFactory<CasualtyModifier> {

	private ModifierAggregator modifierAggregator;
	private Game game;

	public Set<CasualtyModifier> findModifiers(Player<?> player) {

		Set<CasualtyModifier> modifiers = player.getSkillsIncludingTemporaryOnes().stream().flatMap(s -> s.getCasualtyModifiers().stream()).collect(Collectors.toSet());

		long nigglings = Arrays.stream(player.getLastingInjuries()).filter(seriousInjury -> seriousInjury.getInjuryAttribute() == InjuryAttribute.NI).count();
		forNumber((int) nigglings).ifPresent(modifiers::add);

		return modifiers;
	}

	@Override
	public CasualtyModifier forName(String name) {
		return
			modifierAggregator.getCasualtyModifiers().stream()
				.filter(modifier -> modifier.getName().equals(name))
				.findFirst()
				.orElse(fromName(name).orElse(null));
	}

	@Override
	public void initialize(Game game) {
		this.game = game;
		this.modifierAggregator = game.getModifierAggregator();
	}

	private Optional<CasualtyNigglingModifier> fromName(String name) {
		if (StringTool.isProvided(name)) {
			String[] parts = name.split(" ");
			if (ArrayTool.isProvided(parts)) {
				try {
					int count = Integer.parseInt(parts[0]);
					return forNumber(count);
				} catch (NumberFormatException e) {
					game.getApplicationSource().logError(game.getId(), "Passed invalid name as casualty modifier: " + name);
				}
			}
		}
		return Optional.empty();
	}

	private Optional<CasualtyNigglingModifier> forNumber(int number) {
		if (number > 0) {
			String name = number + " Niggling Injur" + (number == 1 ? "y" : "ies");
			return Optional.of(new CasualtyNigglingModifier(name, number));
		}

		return Optional.empty();
	}
}
