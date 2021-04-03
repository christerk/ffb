package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.InjuryAttribute;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.Scanner;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@FactoryType(FactoryType.Factory.SERIOUS_INJURY)
@RulesCollection(Rules.COMMON)
public class SeriousInjuryFactory implements INamedObjectFactory<SeriousInjury> {

	private Set<SeriousInjury> values;
	private SeriousInjury poison, dead;

	public SeriousInjury forName(String pName) {
		for (SeriousInjury seriousInjury : values) {
			if (seriousInjury.getName().equals(pName)) {
				return seriousInjury;
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
		values = new Scanner<>(SeriousInjury.class).getClassesImplementing(game.getOptions()).stream()
			.flatMap(cls -> Arrays.stream(cls.getEnumConstants())).collect(Collectors.toSet());

		values.forEach(value -> {
			if (value.isDead()) {
				dead = value;
			}
			if (value.isPoison()) {
				poison = value;
			}
		});
	}

	public SeriousInjury dead() {
		return dead;
	}

	public SeriousInjury poison() {
		return poison;
	}

	public SeriousInjury forAttribute(InjuryAttribute attribute) {
		return values.stream().filter(value -> value.getInjuryAttribute() == attribute).findFirst().orElse(null);
	}
}
