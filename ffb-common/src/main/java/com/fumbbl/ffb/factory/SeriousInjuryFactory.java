package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.InjuryAttribute;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.Scanner;

import java.util.Set;

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
		values = new Scanner<>(SeriousInjury.class).getEnumValues(game.getOptions());

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
