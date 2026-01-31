package com.fumbbl.ffb.server.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.model.change.ConditionalModelChangeObserver;
import com.fumbbl.ffb.util.Scanner;

import java.util.HashSet;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.COMMON)
@FactoryType(FactoryType.Factory.OBSERVERS)
public class ObserverFactory implements INamedObjectFactory<ConditionalModelChangeObserver> {

	private final Set<ConditionalModelChangeObserver> observers = new HashSet<>();

	@Override
	public ConditionalModelChangeObserver forName(String pName) {
		return null;
	}

	@Override
	public void initialize(Game game) {
		Scanner<ConditionalModelChangeObserver> scanner = new Scanner<>(ConditionalModelChangeObserver.class);
		observers.addAll(scanner.getInstancesImplementing(game.getOptions()));
	}

	public Set<ConditionalModelChangeObserver> getObservers() {
		return observers;
	}
}
