package com.fumbbl.ffb.model.change;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Kalimar
 */
public abstract class ModelChangeObservable {

	private final transient Set<IModelChangeObserver> fObservers;

	public ModelChangeObservable() {
		fObservers = Collections.synchronizedSet(new HashSet<>());
	}

	public void addObserver(IModelChangeObserver pObserver) {
		if (pObserver == null) {
			return;
		}
		fObservers.add(pObserver);
	}

	public void notifyObservers(ModelChange pModelChange) {
		if ((pModelChange == null) || (pModelChange.getChangeId() == null)) {
			return;
		}
		for (IModelChangeObserver observer : fObservers) {
			observer.update(pModelChange);
		}
	}

}
