package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.inducement.InducementCollection;
import com.balancedbytes.games.ffb.inducement.InducementType;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.Scanner;

/**
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.INDUCEMENT_TYPE)
@RulesCollection(Rules.COMMON)
public class InducementTypeFactory implements INamedObjectFactory<InducementType> {
	private InducementCollection types;

	public InducementType forName(String pName) {
		for (InducementType type : types.getTypes()) {
			if (type.getName().equalsIgnoreCase(pName)) {
				return type;
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
		new Scanner<>(InducementCollection.class).getClassesImplementing(game.getOptions())
				.stream().findFirst().ifPresent(types -> this.types = types);

	}

}
