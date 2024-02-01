package com.fumbbl.ffb.factory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.inducement.InducementCollection;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.Scanner;

/**
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.INDUCEMENT_TYPE)
@RulesCollection(Rules.COMMON)
public class InducementTypeFactory implements INamedObjectFactory<InducementType> {
	private InducementCollection types;

	public InducementTypeFactory() {
	}

	public InducementType forName(String pName) {
		for (InducementType type : types.getTypes()) {
			if (type.getName().equalsIgnoreCase(pName)) {
				return type;
			}
		}
		return null;
	}

	public List<InducementType> allTypes() {
		return types.getTypes().stream().sorted(Comparator.comparing(InducementType::getName)).collect(Collectors.toList());
	}

	@Override
	public void initialize(Game game) {
		new Scanner<>(InducementCollection.class).getSubclasses(game.getOptions())
				.stream().findFirst().ifPresent(types -> this.types = types);
	}

}
